package com.jhonny.detective;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.Toast;


public class SendMailTask extends AsyncTask<Object, Object, Object> {
	
	private ProgressDialog statusDialog;
	private Activity sendMailActivity;
	private Resources resources;
	private Context context;
	
	
	public SendMailTask(Activity activity, Resources resources, Context context) {
		this.sendMailActivity = activity;
		this.resources = resources;
		this.context = context;
	}
	
	protected void onPreExecute() {
		try{
			statusDialog = new ProgressDialog(sendMailActivity);
			statusDialog.setMessage(resources.getString(R.string.txt_preparandose));
			statusDialog.setIndeterminate(false);
			statusDialog.setCancelable(false);
			statusDialog.show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	protected Object doInBackground(Object... args) {
	    try{
	    	publishProgress(this.resources.getString(R.string.txt_procesando));
	    	
	    	String from = args[0].toString();
	    	String pass = args[1].toString();
	    	List<String> destinos = new ArrayList<String>();
	    	destinos.addAll((List<String>)args[2]);
	    	String asunto = args[3].toString();
	    	String mensaje = args[4].toString();
	    	Email androidEmail = new Email(from, pass, destinos, asunto, mensaje);
	    	
	    	publishProgress(this.resources.getString(R.string.txt_preparando_msg));
	    	androidEmail.createEmailMessage();
	    	
	    	publishProgress(this.resources.getString(R.string.txt_enviando_mail));
	    	androidEmail.sendEmail();
	    	
	    	publishProgress(this.resources.getString(R.string.txt_mail_enviado));
	    	Toast.makeText(context, R.string.email_enviado, Toast.LENGTH_SHORT).show();
	    	
	    }catch(Exception e){
	    	publishProgress(e.getMessage());
	    }
	    return null;
	}
	
	@Override
	public void onProgressUpdate(Object... values) {
		statusDialog.setMessage(values[0].toString());
	}
	 
	@Override
	public void onPostExecute(Object result) {
		statusDialog.dismiss();
	}
}
