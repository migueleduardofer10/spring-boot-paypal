package com.javatechie.spring.paypal.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
@Service  // Esta anotación define la clase como un servicio en el framework de Spring.
public class PaypalService {

	@Autowired  // Spring inyectará automáticamente un objeto APIContext en esta variable.
	private APIContext apiContext;

	// Método para crear un pago en PayPal.
	public Payment createPayment(
			Double total,
			String currency,
			String method,
			String intent,
			String description,
			String cancelUrl,
			String successUrl) throws PayPalRESTException{

		Amount amount = new Amount();  // Crea un nuevo objeto Amount.
		amount.setCurrency(currency);  // Establece la moneda para el monto.
		// Formatea el total a dos decimales.
		total = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
		amount.setTotal(String.format("%.2f", total));  // Establece el total para el monto.

		Transaction transaction = new Transaction();  // Crea un nuevo objeto Transaction.
		transaction.setDescription(description);  // Establece la descripción para la transacción.
		transaction.setAmount(amount);  // Establece el monto para la transacción.

		List<Transaction> transactions = new ArrayList<>();  // Crea una lista para guardar las transacciones.
		transactions.add(transaction);  // Añade la transacción a la lista.

		Payer payer = new Payer();  // Crea un nuevo objeto Payer.
		payer.setPaymentMethod(method.toString());  // Establece el método de pago.

		Payment payment = new Payment();  // Crea un nuevo objeto Payment.
		payment.setIntent(intent.toString());  // Establece la intención del pago.
		payment.setPayer(payer);  // Establece el pagador del pago.
		payment.setTransactions(transactions);  // Establece las transacciones del pago.

		RedirectUrls redirectUrls = new RedirectUrls();  // Crea un nuevo objeto RedirectUrls.
		redirectUrls.setCancelUrl(cancelUrl);  // Establece la URL de cancelación.
		redirectUrls.setReturnUrl(successUrl);  // Establece la URL de éxito.
		payment.setRedirectUrls(redirectUrls);  // Establece las URLs de redirección para el pago.

		return payment.create(apiContext);  // Crea el pago en PayPal y lo devuelve.

	}

	// Método para ejecutar un pago en PayPal.
	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{

		Payment payment = new Payment();  // Crea un nuevo objeto Payment.
		payment.setId(paymentId);  // Establece el ID del pago.

		PaymentExecution paymentExecute = new PaymentExecution();  // Crea un nuevo objeto PaymentExecution.
		paymentExecute.setPayerId(payerId);  // Establece el ID del pagador.

		return payment.execute(apiContext, paymentExecute);  // Ejecuta el pago y lo devuelve.
	}

}
