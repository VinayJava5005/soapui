/*
 * SoapUI, copyright (C) 2004-2014 smartbear.com
 *
 * SoapUI is free software; you can redistribute it and/or modify it under the
 * terms of version 2.1 of the GNU Lesser General Public License as published by
 * the Free Software Foundation.
 *
 * SoapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details at gnu.org.
 */

package com.eviware.soapui.support.editor.inspectors.auth;

import com.eviware.soapui.impl.support.AbstractHttpRequest;
import com.eviware.soapui.support.components.SimpleBindingForm;
import com.jgoodies.binding.PresentationModel;

import javax.swing.*;

/**
 *
 */
public class BasicAuthenticationForm<T extends AbstractHttpRequest>  extends AbstractAuthenticationForm
{
	private T request;

	public BasicAuthenticationForm(T request)
	{
		this.request = request;
	}

	@Override
	protected JPanel buildUI()
	{
		SimpleBindingForm basicAuthenticationForm = new SimpleBindingForm( new PresentationModel<T>( request ) );
		setBorderAndBackgroundColorOnPanel( basicAuthenticationForm.getPanel() );
		populateBasicForm( basicAuthenticationForm );

		return basicAuthenticationForm.getPanel();
	}

	private void populateBasicForm( SimpleBindingForm basicConfigurationForm )
	{
		initForm( basicConfigurationForm );

		basicConfigurationForm.addSpace( TOP_SPACING );

		basicConfigurationForm.appendTextField( "username", "Username", "The username to use for HTTP Authentication" );
		basicConfigurationForm.appendPasswordField( "password", "Password", "The password to use for HTTP Authentication" );
		basicConfigurationForm.appendTextField( "domain", "Domain", "The domain to use for Authentication(NTLM/Kerberos)" );
	}
}