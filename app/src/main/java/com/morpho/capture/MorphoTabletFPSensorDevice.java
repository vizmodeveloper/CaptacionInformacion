package com.morpho.capture;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.morpho.android.usb.USBManager;
import com.morpho.dao.AuthBfdCap;
import com.morpho.morphosmart.sdk.CallbackMask;
import com.morpho.morphosmart.sdk.CallbackMessage;
import com.morpho.morphosmart.sdk.Coder;
import com.morpho.morphosmart.sdk.DetectionMode;
import com.morpho.morphosmart.sdk.EnrollmentType;
import com.morpho.morphosmart.sdk.ErrorCodes;
import com.morpho.morphosmart.sdk.LatentDetection;
import com.morpho.morphosmart.sdk.MorphoDatabase;
import com.morpho.morphosmart.sdk.MorphoDevice;
import com.morpho.morphosmart.sdk.MorphoImage;
import com.morpho.morphosmart.sdk.ResultMatching;
import com.morpho.morphosmart.sdk.Template;
import com.morpho.morphosmart.sdk.TemplateFVPType;
import com.morpho.morphosmart.sdk.TemplateList;
import com.morpho.morphosmart.sdk.TemplateType;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Observable;
import java.util.Observer;

//import java.awt;
public class MorphoTabletFPSensorDevice implements Observer {

	ImageView im;
	public int openStatus;
	AuthBfdCap objClass;

	public Bitmap bm = null;

	/**
	 * The Morpho device.
	 */
	static MorphoDevice morphoDevice = new MorphoDevice();

	/**
	 * The Morpho database.
	 */
	static MorphoDatabase morphoDatabase = new MorphoDatabase();

	/**
	 * The timeout.
	 */
	private int timeout = 15;

	/**
	 * The acquisition threshold.
	 */
	private int acquisitionThreshold = 0;

	/**
	 * The advanced security levels required.
	 */
	private int advancedSecurityLevelsRequired = 0;

	/**
	 * The template type.
	 */
	private TemplateType templateType = TemplateType.MORPHO_PK_COMP;

	/**
	 * The finger template format.
	 */
	private TemplateType fingerTemplateFormat = TemplateType.MORPHO_PK_COMP;

	/**
	 * The template finger VP type.
	 */
	private TemplateFVPType templateFVPType = TemplateFVPType.MORPHO_NO_PK_FVP;

	/**
	 * The max size template.
	 */
	private int maxSizeTemplate = 512;

	/**
	 * The enroll type.
	 */
	private int enrollType = EnrollmentType.ONE_ACQUISITIONS;

	/**
	 * The latent detection.
	 */
	private int latentDetection = LatentDetection.LATENT_DETECT_ENABLE;

	/**
	 * The coder choice.
	 */
	private Coder coderChoice = Coder.MORPHO_DEFAULT_CODER;

	/**
	 * The detect mode choice.
	 */
	private int detectModeChoice = DetectionMode.MORPHO_ENROLL_DETECT_MODE
			| DetectionMode.MORPHO_FORCE_FINGER_ON_TOP_DETECT_MODE;

	/**
	 * The callback command.
	 */
	private int callbackCmd = CallbackMask.MORPHO_CALLBACK_IMAGE_CMD
			^ CallbackMask.MORPHO_CALLBACK_COMMAND_CMD
			^ CallbackMask.MORPHO_CALLBACK_CODEQUALITY
			^ CallbackMask.MORPHO_CALLBACK_DETECTQUALITY;

	/**
	 * The last image.
	 */
	private byte[] lastImage = null;

	/**
	 * The last image width.
	 */
	private int lastImageWidth = 0;

	/**
	 * The last image height.
	 */
	public int lastImageHeight = 0;

	public byte[] templateBuffer = null;

	/**
	 * last callback msg
	 */

	private String callbackMsg = "";

	private Activity activity;

	private int value;

	public  byte[] rawImage;

	/**
	 * Instantiates a new MorphoTablet fingerprint sensor device.
	 * 
	 * @since 2.0
	 */

	public MorphoTabletFPSensorDevice(AuthBfdCap obj) {

		objClass = obj;
		objClass = obj;

	}

	public void setViewToUpdate(ImageView imageView) {

		im = imageView;

	}

	

	/**
	 * Sets the compression ratio.
	 * 
	 */
	public int open(Activity arg0) {
		// LOGGER.info(">>> MorphoTabletFPSensorDevice open");
		// USBManager.registerReceiver(arg0);
		String sensorName;
		activity = arg0;
		USBManager.getInstance().initialize(arg0,"com.morpho.morphosample.USB_ACTION");
		Integer nbUsbDevice = Integer.valueOf(0);
        int ret = morphoDevice.initUsbDevicesNameEnum(nbUsbDevice);
		sensorName = morphoDevice.getUsbDeviceName(0);
		morphoDevice.closeDevice();
		return morphoDevice.openUsbDevice(sensorName, 0);

	}

	/**
	 * Start com.morpho.capture.
	 * 
	 */

    private Thread hilo ;
	public void startCapture() throws Exception {
		final Observer oThis = this;
		
		this.open(activity);

        hilo = new Thread() {
			@Override
			public void run() {
//				Capture the image
				TemplateList templateList = new TemplateList();
				templateList.setActivateFullImageRetrieving(true);
				final int ret = morphoDevice.capture(timeout,
						acquisitionThreshold, advancedSecurityLevelsRequired,
						1, templateType, templateFVPType, maxSizeTemplate,
						enrollType, latentDetection, coderChoice,
						detectModeChoice, templateList, callbackCmd, oThis);

				try {
					if ((ret == 0)
							&& (templateType != TemplateType.MORPHO_NO_PK_FP)) {

						int nb = templateList.getNbTemplate();

						for (int j = 0; j < nb; j++) {
							FileOutputStream fos = null;
							MorphoImage morphoImage = null;
							try {

								Template t = templateList.getTemplate(j);
								templateBuffer = t.getData();
								// templateBuffer = data;

								morphoImage = templateList.getImage(j);

							} catch (Exception e) {

							} finally {
								if (fos != null) {
									fos.close();
								}
							}

							try {
								int dataWidth = lastImageWidth;
								int dataHeight = lastImageHeight;
								byte[] datafi = (lastImage);
								if (morphoImage != null) {

									datafi = morphoImage.getImage();
									// int sizeImage=datafi.length;
									dataWidth = morphoImage
											.getMorphoImageHeader()
											.getNbColumn();
									dataHeight = morphoImage
											.getMorphoImageHeader().getNbRow();
								}

								lastImage = datafi;
								rawImage = datafi;

								lastImageWidth = dataWidth;
								lastImageHeight = dataHeight;

								updateView("Finger Captured Successfully", ret);

							} catch (Exception e) {
								updateView("Error in Capturing Finger", ret);
								e.printStackTrace();

							}
						}

					} else if (ret == ErrorCodes.MORPHOERR_TIMEOUT) {
						updateView("Capture Timed Out", ret);
					} else {
						updateView("Error in Capturing Finger", ret);
					}

				} catch (FileNotFoundException e) {

				} catch (IOException e) {

				} catch (Exception e) {

				}
			}
		};
        hilo.start();
	}

	/**
	 * Cancel live acquisition.
	 *
	 */	
	public void cancelLiveAcquisition() {
		try {
			morphoDevice.cancelLiveAcquisition();
		} catch (Exception e) {
			

		}
	}

	/**
	 * Release.
	 * 
	 */
	// @Override
	public void release() {
        morphoDevice.closeDevice();
        morphoDevice = null;
	}

	public void updateLiveView(byte[] liveImage, String msg, int imageWidth,
			int imageHeight){

		byte[] Src = liveImage; // Comes from somewhere...
		if (liveImage != null){
			byte[] Bits = new byte[Src.length * 4]; // That's where the RGBA
													// array
													// goes.
			int i;
			for (i = 0; i < Src.length; i++) {
				Bits[i * 4] = Bits[i * 4 + 1] = Bits[i * 4 + 2] = ((byte) ~Src[i]);

				// Invert the source bits
				Bits[i * 4 + 3] = -1;// 0xff, that's the alpha.
			}

			// Now put these nice RGBA pixels into a Bitmap object

			bm = Bitmap.createBitmap(imageWidth, imageHeight,
					Bitmap.Config.ARGB_8888);
			bm.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));
			if (objClass != null && im != null)
				objClass.updateImageView(im, bm, msg,false, 0);

		} else {
			objClass.updateImageView(null, null, msg, false, 0);
			//objClass.setQlyFinger(quality,false);
		}

	}

	// @Override
	public void updateView(String msg, int retError) {

		byte[] Src = lastImage; 
		rawImage = lastImage;
		// Comes from somewhere...
		if (retError == 0) {
			byte[] Bits = new byte[Src.length * 4]; // That's where the RGBA
													// array
													// goes.
			int i;
			for (i = 0; i < Src.length; i++) {
				Bits[i * 4] = Bits[i * 4 + 1] = Bits[i * 4 + 2] = Src[i];

				// Invert the source bits
				Bits[i * 4 + 3] = -1;// 0xff, that's the alpha.
			}

			// Now put these nice RGBA pixels into a Bitmap object

			bm = Bitmap.createBitmap(lastImageWidth, lastImageHeight,
					Bitmap.Config.ARGB_8888);
			bm.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));
			if (bm != null)
                objClass.updateImageView(templateBuffer,im, bm, msg, true, retError);
				//objClass.updateImageView(im, bm, msg, true, retError);

			//objClass.setQlyFinger(quality,true);
		} else {
			objClass.updateImageView(null, null, msg, true, retError);
		}
	}

	public Bitmap getBitmapFromRawImage(byte[] rawImage) {

		byte[] Bits = new byte[rawImage.length * 4]; // That's where the RGBA
		// array
		// goes.
		int i;
		for (i = 0; i < rawImage.length; i++) {
			Bits[i * 4] = Bits[i * 4 + 1] = Bits[i * 4 + 2] = rawImage[i];

			// Invert the source bits
			Bits[i * 4 + 3] = -1;// 0xff, that's the alpha.
		}

		// Now put these nice RGBA pixels into a Bitmap object

		bm = Bitmap.createBitmap(256, 400, Bitmap.Config.ARGB_8888);
		bm.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));

		return bm;

	}
	
	/**
	 * Gets the image from data.
	 * 
	 * @version 2.0
	 * @param numColumns
	 *            the num columns
	 * @param numRows
	 *            the num rows
	 * @param greyscaleImageData
	 *            the grey scale image data
	 * @return the image from data
	 * @post $result != null
	 */
	// @Override
	public Bitmap getImageFromData(int numColumns, int numRows,
			byte[] greyscaleImageData) {
		// message is a low resolution image, display it.
		return BitmapFactory.decodeByteArray(greyscaleImageData, 0,
				greyscaleImageData.length);
	}

	/**
	 * Get live image from the device.
	 * 
	 * @version 2.0
	 * @param numColumns
	 *            height of the image
	 * @param numRows
	 *            width of the image
	 * @param bwImageData
	 *            image data to display
	 * @return Bitmap converted image
	 * @post $result != null
	 */
	// @Override
	public Bitmap getPreviewFromData(int numColumns, int numRows,
			byte[] bwImageData) {
		return getImageFromData(numColumns, numRows, bwImageData);
	}

	/**
	 * Retrieve image.
	 * 
	 */
	// /@Override
	public void retrieveImage() {
		// TODO Complete
	}

	/**
	 * Verify fingerprint.
	 * 
	 * @version 2.0
	 * @param arg0
	 *            the arg0
	 * @param arg1
	 *            the arg1
	 */
	// @Override
	public void verifyFingerprint(byte[] arg0, int arg1) {
		// TODO : in Fact nothing, we'll use the Matcher module for that

	}

	public int verifyMatch(byte[] arg0,byte[] arg1, TemplateType templateType) {
		// in Fact nothing, we'll use the Matcher module for that
		TemplateList listSearch, listRef;
		Template tmpl1, tmpl2;
		Integer matchingScore = 0;

		listSearch = new TemplateList();
		listRef = new TemplateList();

		tmpl1 = new Template();
		tmpl2 = new Template();

		tmpl1.setData(arg0);
		tmpl1.setDataIndex(0);
		tmpl1.setTemplateType(templateType);

		Log.v("", "temp  	" + templateBuffer + "  arg " + arg0);
		tmpl2.setData(arg1);
		tmpl2.setDataIndex(0);
		tmpl2.setTemplateType(templateType);

		listSearch.putTemplate(tmpl1);
		listRef.putTemplate(tmpl2);

		int err = morphoDevice.verifyMatch(5, listSearch, listRef,
				matchingScore);
 		
		return err;
	}

	
	
	public void verify(byte[] arg0) {
		// in Fact nothing, we'll use the Matcher module for that
		final TemplateList listSearch;
		Template tmpl1;
		final ResultMatching matchingScore = new ResultMatching();
		
		listSearch = new TemplateList();

		tmpl1 = new Template();

		tmpl1.setData(arg0);
		tmpl1.setDataIndex(0);
		tmpl1.setTemplateType(TemplateType.MORPHO_PK_ISO_FMR);

		listSearch.putTemplate(tmpl1);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
			int err= morphoDevice.verify(30,5, coderChoice,detectModeChoice,0, listSearch,callbackCmd,MorphoTabletFPSensorDevice.this, matchingScore);
			 updateView("Finger Captured Successfully", err);
			}
		}).start();

		
	}
	/**
	 * The raw header size.
	 */
	private int RAW_HEADER_SIZE = 12;

	public int quality;

	/**
	 * Update.
	 * 
	 * @version 2.0
	 * @param observable
	 *            the observable
	 * @param arg
	 *            the arg
	 * @see java.util.Observer#update(java.util.Observable, Object)
	 * @since 2.0
	 */
	@Override
	public void update(Observable observable, Object arg) {
		try {
			// convert the object to a callback back message.
			CallbackMessage message = (CallbackMessage) arg;
			int type = message.getMessageType();
			switch (type) {
			// --------------------
			// MESSAGES
			// --------------------
			case 1:
				// FingerPrintMessage fingerPrintMessage =
				// FingerPrintMessage.UNKNOWN_MESSAGE;
				// message is a command.
				Integer command = (Integer) message.getMessage();

				// Analyze the command.
				switch (command) {
				case 0:
					/** < The terminal waits for the user's finger. */
					// fingerPrintMessage =
					// FingerPrintMessage.PLACE_FINGER_FOR_ACQUISITION;
					callbackMsg = "Place Finger For Acquisition";
					break;
				case 1:
					/** < The user must move his/her finger up. */
					// fingerPrintMessage = FingerPrintMessage.MOVE_UP;
					callbackMsg = "Move Up";
					break;
				case 2:
					/** < The user must move his/her finger down. */
					// fingerPrintMessage = FingerPrintMessage.MOVE_DOWN;
					callbackMsg = "Move Down";
					break;
				case 3:
					/** < The user must move his/her finger to the left. */
					// fingerPrintMessage = FingerPrintMessage.MOVE_LEFT;
					callbackMsg = "Move Left";
					break;
				case 4:
					/** < The user must move his/her finger to the right. */
					// fingerPrintMessage = FingerPrintMessage.MOVE_RIGHT;
					callbackMsg = "Move Right";
					break;
				case 5:
					/**
					 * < The user must press his/her finger harder for the
					 * device to acquire a larger fingerprint image.
					 */
					// fingerPrintMessage = FingerPrintMessage.PRESS_HARDER;
					callbackMsg = "Press Harder";
					break;
				case 6:
					/**
					 * < The system has detected a latent fingerprint in the
					 * input fingerprint. Please change finger position.
					 */
					// fingerPrintMessage =
					// FingerPrintMessage.REMOVE_YOUR_FINGER;
					callbackMsg = "Remove Finger";
					break;
				case 7:
					/** < User must remove his finger. */
					// fingerPrintMessage =
					// FingerPrintMessage.REMOVE_YOUR_FINGER;
					callbackMsg = "Remove Finger";
					break;
				case 8:
					/** < The finger acquisition was correctly completed. */
					// fingerPrintMessage =
					// FingerPrintMessage.ACQUISITION_COMPLETE;
					callbackMsg = "Finger Capture Complete";
					break;
				}

                Log.d("FINGERPRINT", "IMAGE LIVE");
				updateLiveView(null, callbackMsg, 0, 0);

				break;

			// --------------------
			// IMAGES
			// --------------------
			case 2:
				// message is a low resolution image, display it.
				byte[] image = (byte[]) message.getMessage();
				// quality = (Integer) message.getMessage();
				byte[] imageRAW = new byte[image.length - RAW_HEADER_SIZE];
				MorphoImage morphoImage = MorphoImage
						.getMorphoImageFromLive(image);
				if (morphoImage != null) {
					int imageRowNumber = morphoImage.getMorphoImageHeader()
							.getNbRow();
					int imageColumnNumber = morphoImage.getMorphoImageHeader()
							.getNbColumn();
					System.arraycopy(image, RAW_HEADER_SIZE, imageRAW, 0,
							image.length - RAW_HEADER_SIZE);
					
					updateLiveView(imageRAW, callbackMsg, imageColumnNumber,
							imageRowNumber);

					lastImage = imageRAW;
					lastImageWidth = imageColumnNumber;
					lastImageHeight = imageRowNumber;
				}

				break;
			// --------------------
			// QUALITY
			// --------------------
			case 3:
				quality = (Integer) message.getMessage();
				Log.v("", "quality " + quality);

				break;
			}
		} catch (Exception e) {
		}
	}
}