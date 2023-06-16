package com.javatechie.spring.paypal.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paypal.api.payments.PaymentHistory;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;

@Configuration  // Esta anotación indica que esta clase es una fuente de definiciones de beans que deben crearse en el contexto de Spring.
public class PaypalConfig {

	@Value("${paypal.client.id}")  // Esta anotación permite inyectar valores desde archivos de propiedades en campos de beans.
	private String clientId;
	@Value("${paypal.client.secret}")  // Inyectamos el secret del cliente de PayPal.
	private String clientSecret;
	@Value("${paypal.mode}")  // Inyectamos el modo de PayPal, que puede ser 'sandbox' para pruebas o 'live' para producción.
	private String mode;

	@Bean  // Esta anotación registra un bean en el contenedor de Spring.
	public Map<String, String> paypalSdkConfig() {  // Este método proporciona la configuración para el SDK de PayPal.
		Map<String, String> configMap = new HashMap<>();
		configMap.put("mode", mode);  // Agregamos el modo a la configuración.
		return configMap;
	}

	@Bean
	public OAuthTokenCredential oAuthTokenCredential() {  // Este método proporciona las credenciales OAuth para PayPal.
		// Creamos un nuevo objeto de credenciales OAuth con nuestro cliente ID, secret y la configuración del SDK de PayPal.
		return new OAuthTokenCredential(clientId, clientSecret, paypalSdkConfig());
	}

	@Bean
	public APIContext apiContext() throws PayPalRESTException {  // Este método proporciona el contexto de la API de PayPal.
		// Creamos un nuevo contexto de API con el token de acceso de nuestras credenciales OAuth.
		APIContext context = new APIContext(oAuthTokenCredential().getAccessToken());
		// Establecemos el mapa de configuración para el contexto de la API.
		context.setConfigurationMap(paypalSdkConfig());
		return context;
	}

}
