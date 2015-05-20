package com.morpho.cbm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.morpho.morphosmart.sdk.ErrorCodes;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import carloslobaton.pe.captacioninformacion.R;

/**
 * @author Neha Shukla
 */
public class CommonFunctions {
		
	public static byte[] pathToByte(String path)
	{
		File file = new File(path);
	    int size = (int) file.length();
	    byte[] bytes = new byte[size];
	    try {
	        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
	        buf.read(bytes, 0, bytes.length);
	        buf.close();
	    } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
		return bytes;
	}
	
	public static String generateImage(File directory,byte[] _template,String fileName)
	{
		File respFile = null;
		String path=null;
		FileOutputStream f = null;
		try {
			if (_template != null){
				respFile = new File(directory,fileName);

				f = new FileOutputStream(respFile);
				f.write(_template);
			}

			path = respFile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
			path=null;
		} finally {
			try {
				f.close();
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return path;
		
	}


	
	
	public static String saveFPToFile(File directory,byte[] _template,String fileName){
		File respFile = null;
		String path=null;
		FileOutputStream f = null;
		try {
			if (_template != null){
				respFile = new File(directory,fileName);

				f = new FileOutputStream(respFile);
				f.write(_template);
			}

			path = respFile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
			path=null;
		} finally {
			try {
				f.close();
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return path;
	}

	public static byte[] readISOFMRFromDataDirectory(Context mcContext)
			throws IOException {

		String path = Environment.getExternalStorageDirectory()+ "/vineet_finger_1.iso-fmr";

		/*
		 * String path = getAssets() + "/vineet_finger_1.iso-fmr";
		 */
		File file = new File(path);
		FileInputStream is = new FileInputStream(file);

		// Get the size of the com.com.morpho.cbm.file
		long length = file.length();

		if (length > Integer.MAX_VALUE) {
			throw new IOException("The com.com.morpho.cbm.file is too big");
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("The com.com.morpho.cbm.file was not completely read: "+ file.getName());
		}
		// Close the input stream, all com.com.morpho.cbm.file contents are in the bytes variable
		is.close();
		return bytes;
	}

	public static String getTimeStamp(Context mContext) {

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd h:mm:ss a");
		String currentDate = sdf.format(date);
		System.out.println(currentDate); // 2013/09/24 4:48:16 PM

		return currentDate;
	}

	public static String getAppVersion(Context mContext) {
		PackageInfo pInfo = null;
		try {
			pInfo = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String version = pInfo.versionName;

		String about = "App version is " + version + "\n @Safran Morpho 2013";
		return about;
	}

	/**
	 * To get Internet status
	 */

	public static boolean isNetworkAvailable(Context mcContext, String TAG) {

		ConnectivityManager cm = (ConnectivityManager) mcContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
		
			return true;
		}
		return false;
	}

	public static void alert(Context mContext,  String title,
			String message){
		AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
		alertDialog.setTitle(title);
//		String msg;
//		if (codeError == 0) {
//			msg = mContext.getString(R.string.OP_SUCCESS);
//		} else {
//			String errorInternationalization = convertToInternationalMessage(
//					mContext, codeError);
//			msg = mContext.getString(R.string.OP_FAILED) + "\n"
//					+ errorInternationalization;
//		}
//		msg += ((message.equalsIgnoreCase("")) ? "" : "\n" + message);
		alertDialog.setMessage(message);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		alertDialog.show();
		
	}
	public static void alert(Context mContext, int codeError, String title,
			String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
		alertDialog.setTitle(title);
		String msg;
		if (codeError == 0) {
			msg = mContext.getString(R.string.OP_SUCCESS);
		} else {
			String errorInternationalization = convertToInternationalMessage(
					mContext, codeError);
			msg = mContext.getString(R.string.OP_FAILED) + "\n"
					+ errorInternationalization;
		}
		msg += ((message.equalsIgnoreCase("")) ? "" : "\n" + message);
		alertDialog.setMessage(msg);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		alertDialog.show();
	}

	public static String convertToInternationalMessage(Context mContext,
			int iCodeError) {
		switch (iCodeError) {
		case ErrorCodes.MORPHO_OK:
			return mContext.getString(R.string.MORPHO_OK);
		case ErrorCodes.MORPHOERR_INTERNAL:
			return mContext.getString(R.string.MORPHOERR_INTERNAL);
		case ErrorCodes.MORPHOERR_PROTOCOLE:
			return mContext.getString(R.string.MORPHOERR_PROTOCOLE);
		case ErrorCodes.MORPHOERR_CONNECT:
			return mContext.getString(R.string.MORPHOERR_CONNECT);
		case ErrorCodes.MORPHOERR_CLOSE_COM:
			return mContext.getString(R.string.MORPHOERR_CLOSE_COM);
		case ErrorCodes.MORPHOERR_BADPARAMETER:
			return mContext.getString(R.string.MORPHOERR_BADPARAMETER);
		case ErrorCodes.MORPHOERR_MEMORY_PC:
			return mContext.getString(R.string.MORPHOERR_MEMORY_PC);
		case ErrorCodes.MORPHOERR_MEMORY_DEVICE:
			return mContext.getString(R.string.MORPHOERR_MEMORY_DEVICE);
		case ErrorCodes.MORPHOERR_NO_HIT:
			return mContext.getString(R.string.MORPHOERR_NO_HIT);
		case ErrorCodes.MORPHOERR_STATUS:
			return mContext.getString(R.string.MORPHOERR_STATUS);
		case ErrorCodes.MORPHOERR_DB_FULL:
			return mContext.getString(R.string.MORPHOERR_DB_FULL);
		case ErrorCodes.MORPHOERR_DB_EMPTY:
			return mContext.getString(R.string.MORPHOERR_DB_EMPTY);
		case ErrorCodes.MORPHOERR_ALREADY_ENROLLED:
			return mContext.getString(R.string.MORPHOERR_ALREADY_ENROLLED);
		case ErrorCodes.MORPHOERR_BASE_NOT_FOUND:
			return mContext.getString(R.string.MORPHOERR_BASE_NOT_FOUND);
		case ErrorCodes.MORPHOERR_BASE_ALREADY_EXISTS:
			return mContext.getString(R.string.MORPHOERR_BASE_ALREADY_EXISTS);
		case ErrorCodes.MORPHOERR_NO_ASSOCIATED_DB:
			return mContext.getString(R.string.MORPHOERR_NO_ASSOCIATED_DB);
		case ErrorCodes.MORPHOERR_NO_ASSOCIATED_DEVICE:
			return mContext.getString(R.string.MORPHOERR_NO_ASSOCIATED_DEVICE);
		case ErrorCodes.MORPHOERR_INVALID_TEMPLATE:
			return mContext.getString(R.string.MORPHOERR_INVALID_TEMPLATE);
		case ErrorCodes.MORPHOERR_NOT_IMPLEMENTED:
			return mContext.getString(R.string.MORPHOERR_NOT_IMPLEMENTED);
		case ErrorCodes.MORPHOERR_TIMEOUT:
			return mContext.getString(R.string.MORPHOERR_TIMEOUT);
		case ErrorCodes.MORPHOERR_NO_REGISTERED_TEMPLATE:
			return mContext
					.getString(R.string.MORPHOERR_NO_REGISTERED_TEMPLATE);
		case ErrorCodes.MORPHOERR_FIELD_NOT_FOUND:
			return mContext.getString(R.string.MORPHOERR_FIELD_NOT_FOUND);
		case ErrorCodes.MORPHOERR_CORRUPTED_CLASS:
			return mContext.getString(R.string.MORPHOERR_CORRUPTED_CLASS);
		case ErrorCodes.MORPHOERR_TO_MANY_TEMPLATE:
			return mContext.getString(R.string.MORPHOERR_TO_MANY_TEMPLATE);
		case ErrorCodes.MORPHOERR_TO_MANY_FIELD:
			return mContext.getString(R.string.MORPHOERR_TO_MANY_FIELD);
		case ErrorCodes.MORPHOERR_MIXED_TEMPLATE:
			return mContext.getString(R.string.MORPHOERR_MIXED_TEMPLATE);
		case ErrorCodes.MORPHOERR_CMDE_ABORTED:
			return mContext.getString(R.string.MORPHOERR_CMDE_ABORTED);
		case ErrorCodes.MORPHOERR_INVALID_PK_FORMAT:
			return mContext.getString(R.string.MORPHOERR_INVALID_PK_FORMAT);
		case ErrorCodes.MORPHOERR_SAME_FINGER:
			return mContext.getString(R.string.MORPHOERR_SAME_FINGER);
		case ErrorCodes.MORPHOERR_OUT_OF_FIELD:
			return mContext.getString(R.string.MORPHOERR_OUT_OF_FIELD);
		case ErrorCodes.MORPHOERR_INVALID_USER_ID:
			return mContext.getString(R.string.MORPHOERR_INVALID_USER_ID);
		case ErrorCodes.MORPHOERR_INVALID_USER_DATA:
			return mContext.getString(R.string.MORPHOERR_INVALID_USER_DATA);
		case ErrorCodes.MORPHOERR_FIELD_INVALID:
			return mContext.getString(R.string.MORPHOERR_FIELD_INVALID);
		case ErrorCodes.MORPHOERR_USER_NOT_FOUND:
			return mContext.getString(R.string.MORPHOERR_USER_NOT_FOUND);
		case ErrorCodes.MORPHOERR_COM_NOT_OPEN:
			return mContext.getString(R.string.MORPHOERR_COM_NOT_OPEN);
		case ErrorCodes.MORPHOERR_ELT_ALREADY_PRESENT:
			return mContext.getString(R.string.MORPHOERR_ELT_ALREADY_PRESENT);
		case ErrorCodes.MORPHOERR_NOCALLTO_DBQUERRYFIRST:
			return mContext
					.getString(R.string.MORPHOERR_NOCALLTO_DBQUERRYFIRST);
		case ErrorCodes.MORPHOERR_USER:
			return mContext.getString(R.string.MORPHOERR_USER);
		case ErrorCodes.MORPHOERR_BAD_COMPRESSION:
			return mContext.getString(R.string.MORPHOERR_BAD_COMPRESSION);
		case ErrorCodes.MORPHOERR_SECU:
			return mContext.getString(R.string.MORPHOERR_SECU);
		case ErrorCodes.MORPHOERR_CERTIF_UNKNOW:
			return mContext.getString(R.string.MORPHOERR_CERTIF_UNKNOW);
		case ErrorCodes.MORPHOERR_INVALID_CLASS:
			return mContext.getString(R.string.MORPHOERR_INVALID_CLASS);
		case ErrorCodes.MORPHOERR_USB_DEVICE_NAME_UNKNOWN:
			return mContext
					.getString(R.string.MORPHOERR_USB_DEVICE_NAME_UNKNOWN);
		case ErrorCodes.MORPHOERR_CERTIF_INVALID:
			return mContext.getString(R.string.MORPHOERR_CERTIF_INVALID);
		case ErrorCodes.MORPHOERR_SIGNER_ID:
			return mContext.getString(R.string.MORPHOERR_SIGNER_ID);
		case ErrorCodes.MORPHOERR_SIGNER_ID_INVALID:
			return mContext.getString(R.string.MORPHOERR_SIGNER_ID_INVALID);
		case ErrorCodes.MORPHOERR_FFD:
			return mContext.getString(R.string.MORPHOERR_FFD);
		case ErrorCodes.MORPHOERR_MOIST_FINGER:
			return mContext.getString(R.string.MORPHOERR_MOIST_FINGER);
		case ErrorCodes.MORPHOERR_NO_SERVER:
			return mContext.getString(R.string.MORPHOERR_NO_SERVER);
		case ErrorCodes.MORPHOERR_OTP_NOT_INITIALIZED:
			return mContext.getString(R.string.MORPHOERR_OTP_NOT_INITIALIZED);
		case ErrorCodes.MORPHOERR_OTP_PIN_NEEDED:
			return mContext.getString(R.string.MORPHOERR_OTP_PIN_NEEDED);
		case ErrorCodes.MORPHOERR_OTP_REENROLL_NOT_ALLOWED:
			return mContext
					.getString(R.string.MORPHOERR_OTP_REENROLL_NOT_ALLOWED);
		case ErrorCodes.MORPHOERR_OTP_ENROLL_FAILED:
			return mContext.getString(R.string.MORPHOERR_OTP_ENROLL_FAILED);
		case ErrorCodes.MORPHOERR_OTP_IDENT_FAILED:
			return mContext.getString(R.string.MORPHOERR_OTP_IDENT_FAILED);
		case ErrorCodes.MORPHOERR_NO_MORE_OTP:
			return mContext.getString(R.string.MORPHOERR_NO_MORE_OTP);
		case ErrorCodes.MORPHOERR_OTP_NO_HIT:
			return mContext.getString(R.string.MORPHOERR_OTP_NO_HIT);
		case ErrorCodes.MORPHOERR_OTP_ENROLL_NEEDED:
			return mContext.getString(R.string.MORPHOERR_OTP_ENROLL_NEEDED);
		case ErrorCodes.MORPHOERR_DEVICE_LOCKED:
			return mContext.getString(R.string.MORPHOERR_DEVICE_LOCKED);
		case ErrorCodes.MORPHOERR_DEVICE_NOT_LOCK:
			return mContext.getString(R.string.MORPHOERR_DEVICE_NOT_LOCK);
		case ErrorCodes.MORPHOERR_OTP_LOCK_GEN_OTP:
			return mContext.getString(R.string.MORPHOERR_OTP_LOCK_GEN_OTP);
		case ErrorCodes.MORPHOERR_OTP_LOCK_SET_PARAM:
			return mContext.getString(R.string.MORPHOERR_OTP_LOCK_SET_PARAM);
		case ErrorCodes.MORPHOERR_OTP_LOCK_ENROLL:
			return mContext.getString(R.string.MORPHOERR_OTP_LOCK_ENROLL);
		case ErrorCodes.MORPHOERR_FVP_MINUTIAE_SECURITY_MISMATCH:
			return mContext
					.getString(R.string.MORPHOERR_FVP_MINUTIAE_SECURITY_MISMATCH);
		case ErrorCodes.MORPHOERR_FVP_FINGER_MISPLACED_OR_WITHDRAWN:
			return mContext
					.getString(R.string.MORPHOERR_FVP_FINGER_MISPLACED_OR_WITHDRAWN);
		case ErrorCodes.MORPHOERR_LICENSE_MISSING:
			return mContext.getString(R.string.MORPHOERR_LICENSE_MISSING);
		case ErrorCodes.MORPHOERR_CANT_GRAN_PERMISSION_USB:
			return mContext
					.getString(R.string.MORPHOERR_CANT_GRAN_PERMISSION_USB);
		default:
			return String.format("Unknown error %d, Internal Error = %d",
					iCodeError);
		}
	}
}
