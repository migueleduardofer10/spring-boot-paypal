package com.javatechie.spring.paypal.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
@Controller  // Esta anotación indica que la clase es un controlador de Spring MVC.
public class PaypalController {

	@Autowired  // Esta anotación permite a Spring inyectar un objeto de PaypalService en este controlador.
	PaypalService service;

	public static final String SUCCESS_URL = "pay/success";  // URL para redirigir al usuario después de un pago exitoso.
	public static final String CANCEL_URL = "pay/cancel";  // URL para redirigir al usuario si decide cancelar el pago.

	@GetMapping("/")  // Mapeo de ruta para la página de inicio.
	public String home() {
		return "home";  // Retorna la vista 'home'.
	}

	@PostMapping("/pay")  // Mapeo de ruta para realizar un pago.
	public String payment(@ModelAttribute("order") Order order) {  // El modelo de atributos permite recibir datos del formulario.
		try {
			// Se crea el pago con los detalles de la orden.
			Payment payment = service.createPayment(order.getPrice(), order.getCurrency(), order.getMethod(),
					order.getIntent(), order.getDescription(), "http://localhost:9090/" + CANCEL_URL,
					"http://localhost:9090/" + SUCCESS_URL);
			for(Links link:payment.getLinks()) {
				// Busca la URL de aprobación en los links de la respuesta.
				if(link.getRel().equals("approval_url")) {
					// Si la encuentra, redirige al usuario a la página de PayPal para completar el pago.
					return "redirect:"+link.getHref();
				}
			}

		} catch (PayPalRESTException e) {  // Captura cualquier excepción del SDK de PayPal.
			e.printStackTrace();
		}
		return "redirect:/";  // Si algo va mal, redirige al usuario a la página de inicio.
	}

	@GetMapping(value = CANCEL_URL)  // Mapeo de ruta para el caso en que el usuario cancele el pago.
	public String cancelPay() {
		return "cancel";  // Retorna la vista 'cancel'.
	}

	@GetMapping(value = SUCCESS_URL)  // Mapeo de ruta para el caso de un pago exitoso.
	public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
		// Los parámetros de solicitud permiten recibir el paymentId y el PayerID desde PayPal.
		try {
			// Ejecuta el pago y obtiene el resultado.
			Payment payment = service.executePayment(paymentId, payerId);
			System.out.println(payment.toJSON());  // Imprime el pago en formato JSON.
			if (payment.getState().equals("approved")) {  // Si el estado del pago es 'approved'...
				return "success";  // Retorna la vista 'success'.
			}
		} catch (PayPalRESTException e) {  // Captura cualquier excepción del SDK de PayPal.
			System.out.println(e.getMessage());
		}
		return "redirect:/";  // Si algo va mal, redirige al usuario a la página de inicio.
	}

}
