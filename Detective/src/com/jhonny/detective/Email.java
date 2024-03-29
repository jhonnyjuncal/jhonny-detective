package com.jhonny.detective;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import android.app.Activity;
import android.content.Context;


public class Email implements Serializable {
	
	private static final long serialVersionUID = 1L;
	final String emailPort = "587";// gmail's smtp port
	final String smtpAuth = "true";
	final String starttls = "true";
	final String emailHost = "smtp.gmail.com";
	
	String fromEmail;
	String fromPassword;
	List<String> toEmailList;
	String emailSubject;
	String emailBody;
	
	Properties emailProperties;
	Session mailSession;
	MimeMessage emailMessage;
	
	
	public Email() {
		
	}
	
	public Email(String fromEmail, String fromPassword, List<String> toEmailList,
			String emailSubject, String emailBody) {
		
		this.fromEmail = fromEmail;
		this.fromPassword = fromPassword;
		this.toEmailList = toEmailList;
		this.emailSubject = emailSubject;
		this.emailBody = emailBody;
		
		emailProperties = System.getProperties();
	    emailProperties.put("mail.smtp.port", emailPort);
	    emailProperties.put("mail.smtp.auth", smtpAuth);
	    emailProperties.put("mail.smtp.starttls.enable", starttls);
	}
	
	public MimeMessage createEmailMessage() throws AddressException, MessagingException, UnsupportedEncodingException {
		try{
			mailSession = Session.getDefaultInstance(emailProperties, null);
			emailMessage = new MimeMessage(mailSession);
			emailMessage.setFrom(new InternetAddress(fromEmail, fromEmail));
			
			for(String toEmail : toEmailList){
				emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
			}
			
			emailMessage.setSubject(emailSubject);
			emailMessage.setContent(emailBody, "text/html");// for a html email
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return emailMessage;
	}
	
	public void sendEmail() throws AddressException, MessagingException {
		try{
			Transport transport = mailSession.getTransport("smtp");
			transport.connect(emailHost, fromEmail, fromPassword);
			
			transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
			transport.close();
			
		}catch(AuthenticationFailedException afe){
			afe.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static synchronized boolean enviarPosicionesPorMail(Activity activity, String direccion, Context context){
		boolean resultado = false;
		
		try{
			// lista de direcciones de destino del correo
			List<String> listaDirecciones = new ArrayList<String>();
			listaDirecciones.add(direccion);
			
			String cuenta = new String("detective.android.app@gmail.com");
			String pass = new String("@jho11nny@");
			
			// asunto del correo segun el adioma
			String asunto = context.getResources().getString(R.string.email_asunto);
			
			// tabla con las posiciones
			String tabla = crearTablaCoordenadas(context);
			
			// cuerpo del mensaje
			String mensaje = new String(
					"<img src=\"http://i1244.photobucket.com/albums/gg577/jhonnyjuncal/presentacion2_zps36aeea51.png\" border=\"0\" " +
					"width=\"512\" height=\"256\" /><br>" +
					"Hola,<br>" +
					"Estas son las coordenadas almacenadas en su dispositivo movil:<br><br>" +
					"tabla" +
					"<br><br>" +
					"Le agradezco la confianza depositida en el uso de la aplicacion " +
					"<a href=\"https://play.google.com/store/apps/details?id=com.jhonny.detective\">DETECTIVE</a><br>" +
					"reciba un cordial saludo<br><br>" +
					"Atentamente<br>" +
					"Jhonny Juncal Gonzalez<br>" +
					"Programador y creador de <a href=\"https://play.google.com/store/apps/details?id=com.jhonny.detective\">DETECTIVE</a><br>");
			mensaje = mensaje.replace("tabla", tabla);
			
			SendMailTask sendMailTask = new SendMailTask(activity, context.getResources(), context);
			sendMailTask.execute(cuenta, pass, listaDirecciones, asunto, mensaje);
			
		}catch(Exception ex){
			ex.printStackTrace();
			resultado = false;
		}
		return resultado;
	}
	
	/**
	 * Crea la tabla que contiene las coordenadas a enviar en el correo
	 * @param context
	 * @return String
	 */
	private static String crearTablaCoordenadas(Context context){
		String tabla = new String();
		Locale locale = context.getResources().getConfiguration().locale;
		
		try{
			String cabecera = new String("<table border=\"1\">" +
					"<tr>" +
					"<td align=\"center\" font-style=\"bold\">Fecha</td>" +
					"<td align=\"center\" font-style=\"bold\">Hora</td>" +
					"<td align=\"center\" font-style=\"bold\">Latitud</td>" +
					"<td align=\"center\" font-style=\"bold\">Longitud</td>" +
					"<td align=\"center\" font-style=\"bold\">Ver en Mapa</td>" +
					"</tr>");
			String cuerpo = new String();
			for(ObjetoPosicion op : FileUtil.getListaAssetPosiciones(context, 1)){
				cuerpo += "<tr>" +
						"<td>" + FileUtil.getFechaFormateada(op.getFecha(), locale) + "</td>" +
						"<td>" + FileUtil.getHoraFormateada(op.getFecha(), locale) + "</td>" +
						"<td>" + op.getLatitud() + "</td>" +
						"<td>" + op.getLongitud() + "</td>" +
						"<td><a href=\"http://maps.google.com/maps?q=" + op.getLatitud() + "," + op.getLongitud() + "\">ver en mapa/see in map</a></td>" +
						"</tr>";
			}
			String cierreTabla = new String("</table>");
			
			tabla = cabecera + cuerpo + cierreTabla;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return tabla;
	}
}
