package com.eviware.soapui.impl.rest.mock;

import com.eviware.soapui.impl.wsdl.mock.WsdlMockRunContext;
import com.eviware.soapui.model.mock.MockRequest;
import org.apache.commons.httpclient.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

import static com.eviware.soapui.impl.rest.RestRequestInterface.HttpMethod;
import static org.mockito.Mockito.*;


public class RestMockDispatcherTest
{

	private HttpServletRequest request;
	private HttpServletResponse response;
	private RestMockDispatcher restMockDispatcher;
	private WsdlMockRunContext context;
	private RestMockService restMockService;

	@Before
	public void setUp()
	{
		createRestMockDispatcher();
	}

	@Test
	public void afterRequestScriptIsCalled() throws Exception
	{
		when( restMockService.getPath()).thenReturn( "/" );
		when( request.getPathInfo() ).thenReturn( "/" );
		RestMockResult mockResult = ( RestMockResult )restMockDispatcher.dispatchRequest( request, response );

		verify( restMockService ).runAfterRequestScript( context, mockResult );
	}

	@Test
	public void onRequestScriptIsCalled() throws Exception
	{
		RestMockResult mockResult = ( RestMockResult )restMockDispatcher.dispatchRequest( request, response );

		verify( restMockService ).runOnRequestScript( any( WsdlMockRunContext.class ), any( MockRequest.class ) );
	}

	@Test
	public void onRequestScriptOverridesRegularDispatching() throws Exception
	{
		/*
			When onRequestScript returns a MockResult instance then regular dispatching is ignored.
			This tests verify when script returns MokResult instance we bypass regular dispatching.

		 */

		RestMockResult  restMockResult = mock(RestMockResult.class );
		when( restMockService.runOnRequestScript( any( WsdlMockRunContext.class ), any( MockRequest.class ))).thenReturn( restMockResult );

		restMockDispatcher.dispatchRequest( request, response );

		// we would like to verify that dispatchRequest is never called but it is hard so we verify on this instead
		verify( restMockService, never() ).findBestMatchedOperation( anyString(), any( HttpMethod.class ) );
	}

	@Test
	public void shouldReturnNoResponseFoundWhenThereIsNoMatchingAction() throws Exception
	{
		when( restMockService.findBestMatchedOperation( anyString(), any( HttpMethod.class ) ) ).thenReturn( null );
		when( restMockService.getPath()).thenReturn( "/" );
		when( request.getPathInfo() ).thenReturn( "/" );

		restMockDispatcher.dispatchRequest( request, response );

		verify( response ).setStatus( HttpStatus.SC_NOT_FOUND );
	}


	@Test
	public void shouldResponseWhenServicePathMatches() throws Exception
	{
		RestMockAction action = mock(RestMockAction.class);
		when( restMockService.findBestMatchedOperation( "/api", HttpMethod.DELETE ) ).thenReturn( action );
		when( restMockService.getPath()).thenReturn( "/sweden" );
		when( request.getPathInfo() ).thenReturn( "/sweden/api" );

		restMockDispatcher.dispatchRequest( request, response );

		verify( action ).dispatchRequest( any(RestMockRequest.class) );
	}

	@Test
	public void shouldResponseWhenPathMatches() throws Exception
	{
		RestMockAction action = mock(RestMockAction.class);
		when( restMockService.findBestMatchedOperation( "/api", HttpMethod.DELETE ) ).thenReturn( action );
		when( restMockService.getPath()).thenReturn( "/" );
		when( request.getPathInfo() ).thenReturn( "/api" );

		restMockDispatcher.dispatchRequest( request, response );

		verify( action ).dispatchRequest( any(RestMockRequest.class) );
	}

	@Test
	public void returnsErrorOnrequestScriptException() throws Exception
	{
		Exception runTimeException = new IllegalStateException( "wrong state" );
		when( restMockService.runOnRequestScript( any( WsdlMockRunContext.class ), any( MockRequest.class ))).thenThrow( runTimeException );

		restMockDispatcher.dispatchRequest( request, response );

		verify( response ).setStatus( HttpStatus.SC_INTERNAL_SERVER_ERROR );
	}


	private void createRestMockDispatcher()
	{
		request = mock( HttpServletRequest.class );
		Enumeration enumeration = mock( Enumeration.class );
		when( request.getHeaderNames() ).thenReturn( enumeration );
		when( request.getMethod() ).thenReturn( HttpMethod.DELETE.name() );

		response = mock( HttpServletResponse.class );
		restMockService = mock( RestMockService.class );
		context = mock( WsdlMockRunContext.class );

		restMockDispatcher = new RestMockDispatcher( restMockService, context );
	}
}
