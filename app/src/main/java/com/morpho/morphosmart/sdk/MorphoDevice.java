package com.morpho.morphosmart.sdk;

import com.morpho.android.usb.USBManager;

import java.io.Serializable;
import java.util.Observer;

/**
 * Class communicating with the MorphoSmart™
 * This class performs operations related to a physical MorphoSmart™;.
 * The internal variable members are filled after a correct connection to the MorphoSmart™;
 * (after a successful OpenDevice() call).
 * 
 */

public class MorphoDevice implements Serializable, Cloneable
{	
	private static final long serialVersionUID = 1L;
	private static MorphoDeviceNative morphoDeviceNative = new MorphoDeviceNative();
	private Long morphoDevicePointerCPP  = Long.valueOf(0);
	
	// Configuration Keys
	/** @brief Sensor Window Position Configuration Key. */
	public static final int CONFIG_SENSOR_WIN_POSITION_TAG = 0x0E10;
	/** @brief UI Configuration Key. */
	public static final int CONFIG_UI_CONFIG_TAG = 0x1410;
	/** @brief UI Reset Key. */
	public static final int CONFIG_UI_RESET_TAG = 0x1411;	
	
	protected boolean cppMemOwn = false;
	
	/** constructor */
	public MorphoDevice()
	{
		long cppPtr = morphoDeviceNative.getCPPInstance();
		
		if (cppPtr != 0)
		{
			this.cppMemOwn = true;
			this.morphoDevicePointerCPP = cppPtr;
			//Log.d("MORPHO_DEVICE","pointer = <"+ cppPtr+">");
		}
		else
		{
			try{
				throw new MorphoSmartException("cppPtr is null");
			} catch (MorphoSmartException e) {
				e.printStackTrace();
			}	
		}
	}
	
	/**copy constructor */
	public MorphoDevice(MorphoDevice device)
	{
		if(!device.cppMemOwn)
		{
			long cppPtr = morphoDeviceNative.getCPPInstance(device.morphoDevicePointerCPP);
			
			if (cppPtr != 0)
			{
				this.cppMemOwn = true;
				this.morphoDevicePointerCPP = cppPtr;
			}
			else
			{
				try {
					throw new MorphoSmartException("cppPtr is null");
				} catch (MorphoSmartException e) {
					e.printStackTrace();
				}	
			}
		}
		else
		{
			try {
				throw new MorphoSmartException("cppMemOwn is true");
			} catch (MorphoSmartException e) {
				e.printStackTrace();
			}			
		}
	}
		
	/** destructor */
	@Override
	protected void finalize()
	{
		if(this.cppMemOwn)
		{
			this.closeDevice();
			morphoDeviceNative.deleteInstance(this.morphoDevicePointerCPP);
			this.cppMemOwn = false;
		}
	}

	@Override
	public Object clone()
	{
		return new MorphoDevice(this);
	}
	
	public void setMorphoDeviceNativePointerCPP(long morphoDevicePointerCPP)
	{
		this.morphoDevicePointerCPP = morphoDevicePointerCPP;	
		this.cppMemOwn = true;
	}	
	
	/** This function returns a database instance
	 * @param databaseIndex Database index (first index is 0).
	 * @param morphoDatabase Database instance 
	 * @return values:
		- MORPHO_OK The execution of the function was successful.  
		- MORPHOERR_BADPARAMETER Index is higher than (MORPHO_NB_DATABASE_MAX-1).  
		- MORPHOERR_CORRUPTED_CLASS Class has been corrupted.  
*/
	public int getDatabase(int databaseIndex, MorphoDatabase morphoDatabase)
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		
		return morphoDeviceNative.getDatabase(morphoDevicePointerCPP, databaseIndex, morphoDatabase);
	}
	/**This function cancels a finger acquisition. 
	 * @param None
	 * @return values:
		- MORPHO_OK The execution of the function was successful.  
		- MORPHOERR_CORRUPTED_CLASS Class has been corrupted.  
*/
	public int cancelLiveAcquisition()
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		
		return morphoDeviceNative.cancelLiveAcquisition(morphoDevicePointerCPP);
	}

	/** This function captures live fingers and extracts their templates.The number of fingers can be specified. The template is calculated afte
	 * one or three fingerprint image acquisitions (the user has to put each finger 1 or 3 times on the sensor). We strongly recommend getting 3 images
	 * for enrollment purpose, and 1 image for verification purpose. An enrollment based on 3 images will increase the system accuracy. 
	 * To obtain the best accuracy, it is strongly recommended to use the fore, the thumb or the middle fingers. Fingerprint quality and advanced security levels compatibility
	 * (MorphoSmart™; FINGER VP only) are only available if export of templates is activated. TemplateList.GetFVPTemplate must be called to retreive multimodal template. 
	 * TemplateList.GetTemplate must be called to retreive fingerprint template. 
	 * @param timeout Finger detection timeout in seconds. Its value must be between 0 and 0xFFFF. 0 corresponds to an infinite timeout. 
	 * @param acquisitionThreshold Minimum value of fingerprint quality. This value can be 0 (strongly recommended) or any value between 20 and 100.  
	 * @param advancedSecurityLevelsRequired This parameter is supported only by MorphoSmart™; FINGER VP. This parameter can be set to
		- 1 : to get multimodal templates compatible with advanced security levels (levels greater than Standard) 
		- 0 : to get information about the multimodal templates compatibilty with advanced security levels. 
		- 0xFF : neither be informed nor force multimodal templates compatibility with advanced security levels. 
		- If i_uc_enrolType is set to one (1), this value can only be set to 0xFF.  
	 * @param fingerNumber The number of fingers to enroll. This function can enroll 1 or 2 fingers. Set this value to 1 to enroll 1 finger per user. Set this value to 2 to enroll 2 fingers per user.  
	 * @param templateType Indicates the template acquisition format. The template format should be MORPHO_PK_COMP. The other template formats (MORPHO_PK_COMP_NORM, MORPHO_PK_MAT, MORPHO_PK_MAT_NORM, MORPHO_PK_ANSI_378, MORPHO_PK_MINEX_A, MORPHO_PK_ISO_FMR, MORPHO_PK_ISO_FMC_NS or MORPHO_PK_ISO_FMC_CS) are also supported but Morpho recommends using them only for compatibility with existing systems or specific usage. On MorphoSmart™; FINGER VP, put MORPHO_NO_PK_FP.  
	 * @param templateFVPType Indicates the multimodal template acquisition format. The template format should be MORPHO_PK_FVP.  
	 * @param maxSizeTemplate Defines the format of the exported minutiae.
		- Set this value to 1 to export the minutiae with its default size. 
		- For MORPHO_PK_COMP fingerprint template format only, this field can be set to a value from 170 (0xAA) to 255 (0xFF) to limit the size of the fingerprint template. If the fingerprint template size is higher than the required value, it is compressed to match the requirement before being included in the reply, otherwise the fingerprint template is included without modification. It means that the fingerprint template size is less or equal to the specified value. 
		- For MORPHO_PK_ANSI_378, MORPHO_PK_ISO_FMC_CS, MORPHO_PK_ISO_FMC_NS and MORPHO_PK_ISO_FMR fingerprint template formats, this field can be set to a value from 2 (0x02) to 255 (0xFF) to limit the number of PK (minutiae) in the fingerprint template. 
 	 * @param enrollType Specifies the number of fingerprint image acquisitions. Allowed values are 0, 1 and 3. We strongly recommend setting this value to 0 (default value) or 3 for enrollment purpose to increase the system performances: in this case, the template is generated from a consolidation calculation of three consecutive acquisitions of the same fingerprint. It is also possible to set this value to 1 for verification purpose. In this case, it is not possible to save the record in the internal database: in this case, the template is generated from one single fingerprint acquisition. On MorphoSmart™; FINGER VP, the value 1 is deprecated.  
 	 * @param latentDetection Set to a LATENT_DETECT_DISABLEvalue to disable the latent detection mechanism: the default settings 
		- Set to a LATENT_DETECT_ENABLE value to enable the latent detection mechanism. This should be enabled when capturing the template for verification performed on the PC (i.e. when i_uc_enrolType is set to 1).  
	 * @param coderChoice contains the biometric coder to use (MORPHO_MSO_V9_CODER or MORPHO_MSO_V9_JUV_CODER). Morpho recommends using MORPHO_MSO_V9_CODER. Please refer to the MorphoSmartHostInterface document for details.  
	 * @param detectModeChoice Bitmask of the following:
		- MORPHO_VERIF_DETECT_MODE: more permissive mode than default; MorphoSmart™; detects more easily finger presence, but might issue lower quality templates. 
		- MORPHO_ENROLL_DETECT_MODE: strongest detection mode (default mode). 
		- MORPHO_WAKEUP_LED_OFF: (only available on MorphoSmart™; MSO FFD) leds are turned off while waiting for a finger (impedance wakeup). 
		- MORPHO_FORCE_FINGER_ON_TOP_DETECT_MODE: (not available on MorphoSmart™; CBM-V) force the finger to cover the top of the capture area to increase quality. 
		- MORPHO_WAKEUP_LED_ON: (only available on MorphoSmart™; FINGER VP) leds are turned on while waiting for a finger.
	 * @param templateList Resulting templates list
	 * @param callbackCmd Binary mask with CallbackMask elements. This mask describes the
	 * asynchronous status events that will trig the callback function.
	 * Set this parameter to 0 if you do not want any asynchronous status to be received.
	 * For example MORPHO_CALLBACK_COMMAND_CMD | MORPHO_CALLBACK_IMAGE_CMD means we want
	 * to receive the command status (move finger more left, remove finger,...) and
	 * low-resolution images.
	 * @param callback User context called on the reception of asynchronous status. null if not used.
	 * @return values:
		- MORPHO_OK The execution of the function was successful.  
		- MORPHOERR_INTERNAL An error occurred during the execution of the function.  
		- MORPHOERR_BADPARAMETER One or more input parameters are out of range.  
		- MORPHOERR_TIMEOUT The finger detection timeout has expired.  
		- MORPHOERR_MEMORY_PC Not enough memory on the PC.  
		- MORPHOERR_SAME_FINGER User gave the same finger twice.  
		- MORPHOERR_PROTOCOLE Communication protocol error.  
		- MORPHOERR_INVALID_PK_FORMAT Invalid template format  
		- MORPHOERR_CORRUPTED_CLASS Class has been corrupted.  
		- MORPHOERR_FFD False Finger Detected  
		- MORPHOERR_MOIST_FINGER The finger can be too moist or the scanner is wet.  
		- MORPHOERR_LICENSE_MISSING A required license is missing (MorphoSmart™; FINGER VP only).  
		- MORPHOERR_ADVANCED_SECURITY_LEVEL_MISMATCH Failed to make a multimodal template compatible with advanced security levels (MorphoSmart™; FINGER VP only).  
		- MORPHOERR_BAD_FINAL_FINGER_PRINT_QUALITY Failed to capture the fingerprint with a quality greater than or equal to the specified threshold.  
		- MORPHOERR_KEY_NOT_FOUND The specified key is missing, unable to encrypt biom�triques data.  
*/
	public int capture(
						int timeout,
						int acquisitionThreshold,
						int advancedSecurityLevelsRequired,
						int fingerNumber,
						TemplateType templateType,
						TemplateFVPType templateFVPType,
						int maxSizeTemplate,
						int enrollType,
						int latentDetection,
						Coder coderChoice,
						int detectModeChoice,
						TemplateList templateList,
						int callbackCmd,
						Observer callback
						)
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		
		return morphoDeviceNative.capture(morphoDevicePointerCPP, timeout, acquisitionThreshold, advancedSecurityLevelsRequired, fingerNumber, templateType.getCode(), templateFVPType.getCode(), maxSizeTemplate, enrollType, latentDetection, coderChoice.getCode(), detectModeChoice, templateList, callbackCmd, callback);
	}
	
	/** This function closes the MorphoSmart™; communication link. 
	 * @param None
	 * @return values:
		- MORPHO_OK The execution of the function was successful  
		- MORPHOERR_CLOSE_COM Can not close the MorphoSmart™; communication link  
		- MORPHOERR_CORRUPTED_CLASS Class has been corrupted.  
		- MORPHOERR_USER User error */
	public int closeDevice()
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		return morphoDeviceNative.closeDevice(morphoDevicePointerCPP);
	}
	
	/** MORPHO USE ONLY  */
	public int enableCS(boolean enable)
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		return morphoDeviceNative.enableCS(morphoDevicePointerCPP, enable);
	}
	
	/** This method enables or disables the encryption of exported biometric data and sets the diversification data to be concatenated to image/template before encryption. 
	 * @param enable This variable is used to indicate the activation or deactivation encryption.
	 * @param diversificationData Diversification data to be concatenated to image/template before encryption.
	 * @return values:
		- MORPHO_OK The execution of the function was successful.  
		- MORPHOERR_BADPARAMETER One or more input parameters are out of range.  
		- MORPHOERR_INVALID_CLASS The class has been destroyed  
		- MORPHOERR_CORRUPTED_CLASS Class has been corrupted. 
	 */
	public int enableDataEncryption(boolean enable, String diversificationData)
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		return morphoDeviceNative.enableDataEncryption(morphoDevicePointerCPP, enable, diversificationData);
	}
	/** This function opens the MorphoSmart™; communication link and tests it with a basic exchange with the MorphoSmart™;. 
	 * @param serialPortNumber -1: USB connection (MSO300) 
		- 0: Auto detection mode. A command is sent to all available serial COM ports. The detection process is stopped with the first MorphoSmart™; found. We do not recommend this mode because it may cause problem with some other plug-in peripherals, and this mode is slower than the direct option. 
		- >0: Serial COM port number (MSO200): 1 means COM1, 2 means COM2, ...  
	 * @param baudRate The default value is 115200 bauds.  
	 * @return values:
		- MORPHO_OK The execution of the function was successful.  
		- MORPHOERR_CONNECT Cannot connect to the MorphoSmart™;.  
		- MORPHOERR_CORRUPTED_CLASS Class has been corrupted.  
		- MORPHOERR_NO_SERVER The Morpho MorphoSmart Service Provider Usb Server is stopped or not installed.  
	*/
	public int openDevice(int serialPortNumber, int baudRate)
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
				
		int ret = USBManager.isPermissionGranted();
		
		if( ret == ErrorCodes.MORPHO_OK)
		{
			return morphoDeviceNative.openDevice(morphoDevicePointerCPP, serialPortNumber, baudRate);
		}
		else
		{
			return ret;
		}
	}
	
	/** get the string that describes the product reference.
	 * @param None 
	 * @return product descriptor */
	public String getProductDescriptor()
	{
		return morphoDeviceNative.getProductDescriptor(morphoDevicePointerCPP);
	}
	
	/** get the string that describes the optical sensor.
	 * @param None
	 * @return sensor descriptor */
	public String getSensorDescriptor()
	{
		return morphoDeviceNative.getSensorDescriptor(morphoDevicePointerCPP);
	}
	
	/** get the string that describes the condicion release.
	 * @param None
	 * @return condicion descriptor */
	public String getSoftwareDescriptor()
	{
		return morphoDeviceNative.getSoftwareDescriptor(morphoDevicePointerCPP);
	}
	
	/** This function opens the USB communication link and tests it with a basic exchange with the MorphoSmart™;. 
	 * @param sensorName A string containing the name of the MSO (product number-serial number), returned by the GetUsbDevicesNameEnum function.  
	 * @param timeOut Timeout in millisecond. 
	 * @return values:
		- MORPHO_OK The execution of the function was successful.  
		- MORPHOERR_CONNECT Cannot connect to the MorphoSmart™;.  
		- MORPHOERR_CORRUPTED_CLASS Class has been corrupted.  
		- MORPHOERR_USB_DEVICE_NAME_UNKNOWN The specified USB device is not plugged  
		- MORPHOERR_NO_SERVER The Morpho MorphoSmart Service Provider Usb Server is stopped or not installed.  
		- MORPHOERR_BADPARAMETER One or more input parameters are out of range. */
	public int openUsbDevice(String sensorName,int timeOut)
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
				
		int ret = USBManager.isPermissionGranted();
		
		if( ret == ErrorCodes.MORPHO_OK)
		{		
			return morphoDeviceNative.openUsbDevice(morphoDevicePointerCPP, sensorName, timeOut);
		}
		else
		{
			return ret;
		}
	}
	
	/** This function modifies the value of the specified parameter of the MorphoSmart™; device configuration. The modification is ignored until the MorphoSmart™; device is restarted. A power off of the MorphoSmart™; device does not restore the default values: the modified values are saved.
	 * @param tag Parameter Identifier :
		-CONFIG_SENSOR_WIN_POSITION_TAG: Sensor Window Position (only available for MorphoSmart™; CBM). Accepted values are 0, 1, 2, 3, 4, 5, 6 or 7.
	 * @param paramValue Parameter value 
	 * @return values:
		- MORPHO_OK The function was successful.  
		- MORPHOERR_INTERNAL An error occurred during the execution of the function.  
		- MORPHOERR_BADPARAMETER One or more input parameters are out of range.  
	 */
	public int setConfigParam(int tag,String paramValue)
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		return morphoDeviceNative.setConfigParam(morphoDevicePointerCPP, tag, paramValue);
	}
	
	/**This function sets the security level for finger acquisition functions. This level can be retrieved at any moment with GetSecurityLevel.
	 * @param securityLevel Possible values:
		- MorphoSmart™; MSO FFD:
		- FFD_SECURITY_LEVEL_LOW_HOST 
		- FFD_SECURITY_LEVEL_MEDIUM_HOST 
		- FFD_SECURITY_LEVEL_HIGH_HOST 
		- MorphoSmart™; FINGER VP:
		- MULTIMODAL_SECURITY_STANDARD 
		- MULTIMODAL_SECURITY_MEDIUM 
		- MULTIMODAL_SECURITY_HIGH 
	@return values:
		- MORPHO_OK The execution of the function was successful.  
		- MORPHOERR_INVALID_CLASS The class has been destroyed  
		- MORPHOERR_CORRUPTED_CLASS Class has been corrupted.  
*/
	public int setSecurityLevel(int securityLevel)
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		return morphoDeviceNative.setSecurityLevel(morphoDevicePointerCPP, securityLevel);
	}
	
	/** This function returns the security level set with SetSecurityLevel.
	 * @param None 
	 * @return Return values:
		- MORPHO_OK The execution of the function was successful.  
		- MORPHOERR_INVALID_CLASS The class has been destroyed  
		- MORPHOERR_CORRUPTED_CLASS Class has been corrupted.  
		- MORPHOERR_BADPARAMETER o_pi_SecurityLevel is an invalid pointer.  
	 */
	public int getSecurityLevel()
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		return morphoDeviceNative.getSecurityLevel(morphoDevicePointerCPP);
	}
	/**This function returns the type of the connection to a specific MorphoSmart™; of a MorphoDevice object.
	 * @param None 
	 * @return  Communication type*/
	public int getComType()
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		return morphoDeviceNative.getComType(morphoDevicePointerCPP);
	}
	/**This function retrieves the value of a specified parameter of MorphoSmart™; device configuration. 
	 * @param parameterIdentifier Parameter Identifier :
		- CONFIG_SENSOR_WIN_POSITION_TAG: Sensor Window Position (only available for MorphoSmart™; CBM). 
		- CONFIG_UI_CONFIG_TAG: UI Configuration (only available for MorphoSmart™; FINGER VP) 
		- CONFIG_UI_RESET_TAG: UI Reset (only available for MorphoSmart™; FINGER VP)
		@return the value of the parameter 
	*/
	public String getConfigParam(int parameterIdentifier)
	{
		return morphoDeviceNative.getConfigParam(morphoDevicePointerCPP, parameterIdentifier);
	}
	
	/** This function returns a descriptor in the form of a string.
	 * @param descriptorIdentifier Required descriptor. Available descriptors are:
		- BINDESC_VERSION 
		- BINDESC_MAX_USER 
		- BINDESC_MAX_DB 
		- BINDESC_SOFT 
		- BINDESC_FLASH_SIZE 
		- BINDESC_PRODUCT_NAME 
		- BINDESC_PID 
		- BINDESC_SN 
		- BINDESC_OEM_PID 
		- BINDESC_OEM_SN 
		- BINDESC_SENSOR_ID 
		- BINDESC_SENSOR_SN 
		- BINDESC_LICENSES 
		- BINDESC_CUSTOM_DESCRIPTOR    
	 * @return values:
		- MORPHO_OK The execution of the function was successful.  
		- MORPHOERR_INVALID_CLASS The class has been destroyed.  
		- MORPHOERR_CORRUPTED_CLASS Class has been corrupted.  
		- MORPHOERR_BADPARAMETER String buffer is invalid or too small.
	*/
	public String getStringDescriptorBin(int descriptorIdentifier)
	{
		return morphoDeviceNative.getStringDescriptorBin(morphoDevicePointerCPP, descriptorIdentifier);
	}
	
	/** This function returns a descriptor in the form of an integer.
	 * @param descriptorIdentifier Required descriptor. Available descriptors are:
		- BINDESC_MAX_USER 
		- BINDESC_MAX_DB 
		- BINDESC_FLASH_SIZE   
	 * @return values:
		- MORPHO_OK The execution of the function was successful.  
		- MORPHOERR_INVALID_CLASS The class has been destroyed.  
		- MORPHOERR_CORRUPTED_CLASS Class has been corrupted.  
		- MORPHOERR_BADPARAMETER String buffer is invalid or too small. */
	public int getIntDescriptorBin(int descriptorIdentifier)
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		return morphoDeviceNative.getIntDescriptorBin(morphoDevicePointerCPP, descriptorIdentifier);
	}
	/**This function releases FFD log buffer allocated by MorphoDevice.GetFFDLogs method.
	 * @param log : Log line buffer to release 
	 * @return values:
		- MORPHO_OK The release was ok.  
	 */
	public int releaseFFDLogs(String log){
		return morphoDeviceNative.releaseFFDLogs(morphoDevicePointerCPP,log);
	} 
	
	/** This function retrieves FFD logs. Log data received from the terminal are formatted and concatenated into a output buffer separated by CR/LF.
	 * This output buffer is sized according to the number of received events and should be released using the MorphoDevice.ReleaseFFDLogs method.
	 * 
	 * @param None
	 * @return values:
		- MORPHO_OK The retrieve was ok.  
		- MSO_BAD_PARAMETER Wrong device  
		- COM_ERR_NOT_OPEN Communication is not initiated  
		- COM_ERR Error during communication  
		- COM_RECEIVE_ILV_INVALID Unknown sent ILV  
		- COM_STOP Communication stopped  
		- COM_ERROR_SYNCHRO_I Unexpected returned ILV 
*/
	public String getFFDLogs()
	{
		return morphoDeviceNative.getFFDLogs(morphoDevicePointerCPP);
	}
	
	/**
	 * When the biometric terminal returns an internal error,
	 * the SDK returns MORPHOERR_INTERNAL. GetInternalError() retrieves this internal error. This internal error may help to diagnose internal problems and should be sent to Morpho for further analysis.
	 * @param None
	 * @return values:
		- Internal biometric terminal error.  
	 */
	
	public int getInternalError()
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		return morphoDeviceNative.getInternalError(morphoDevicePointerCPP);
	}
	
	/**This function captures live fingers and extracts its image in full resolution. To obtain the best accuracy, it is strongly recommended to use the fore, the thumb or the middle fingers. 
	MorphoDevice.GetImage() is a fingerprints acquisition. This function performs the following process:
	*	- Lighting of the MorphoSmart™; sensor : the MorphoSmart™; terminal starts the reception of images which come from the MorphoSmart™; sensor, 
	*	- Searching of a fingerprint within each received image, 
	*	- Evaluation of the fingerprint quality until obtaining a sufficient quality, 
	*	- False finger detection (if applicable, i.e. for MSOxx1), 
	*	- Emission of the image that contains the sufficient fingerprint quality. 
	*	- For a well use of this function, you had better to set the timeout in order that the user could position his finger: we recommend a timeout value between 15 and 60 seconds, a 30 seconds timeout would be ideal.
	*	
	* @param timeOut Finger detection timeout in seconds. Its value must be between 0 and 0xFFFF. 0 corresponds to an infinite timeout.  
	* @param acquisitionThreshold Minimum value of fingerprint quality. This value can be 0 (strongly recommended) or any value between 20 and 100.  
	* @param compressAlgo Compression algorithm to be used to compress the fingerprint image. Available algorithms are:
	*	- MORPHO_NO_COMPRESS 
	*	- MORPHO_COMPRESS_V1 
	*	- MORPHO_COMPRESS_WSQ  
	* @param compressRate Compression rate used by the fingerprint image compression algorithm:
	*	- useless for MORPHO_NO_COMPRESS and MORPHO_COMPRESS_V1 algorithms (must be set to 0). 
	*	- can vary between 2 and 256 for MORPHO_COMPRESS_WSQ algorithm, usual recommended value is 10.  
	* @param detectModeChoice Bitmask of the following:
	*	- MORPHO_VERIF_DETECT_MODE: more permissive mode than default; MorphoSmart™; detects more easily finger presence, but might issue lower quality templates. 
	*	- MORPHO_ENROLL_DETECT_MODE: strongest detection mode (default mode). 
	*	- MORPHO_WAKEUP_LED_OFF: (only available on MorphoSmart™; MSO FFD) leds are turned off while waiting for a finger (impedance wakeup). 
	*	- MORPHO_FORCE_FINGER_ON_TOP_DETECT_MODE: (not available on MorphoSmart™; CBM-V) force the finger to cover the top of the capture area to increase quality. 
	*	- MORPHO_WAKEUP_LED_ON: (only available on MorphoSmart™; FINGER VP) leds are turned on while waiting for a finger.  
	* @param latentDetection Set to a LATENT_DETECT_DISABLE value to disable the latent detection mechanism: the default settings 
	*	- Set to a LATENT_DETECT_ENABLE value to enable the latent detection mechanism. This should be enabled when capturing the template for verification performed on the PC (i.e. when i_uc_enrolType is set to 1).
	* @param morphoImage resulting image
	* @param callbackCmd Binary mask with CallbackMask elements
	* @param callback User context called on the reception of asynchronous status. null if not used.
	* @return values:
	*	- MORPHO_OK The execution of the function was successful.  
	*	- MORPHOERR_INTERNAL An error occurred during the execution of the function.  
	*	- MORPHOERR_BADPARAMETER One or more input parameters are out of range.  
	*	- MORPHOERR_TIMEOUT The finger detection timeout has expired.  
	*	- MORPHOERR_MEMORY_PC Not enough memory on the PC.  
	*	- MORPHOERR_PROTOCOLE Communication protocol error.  
	*	- MORPHOERR_BAD_COMPRESSION Invalid compression type.  
	*	- MORPHOERR_CORRUPTED_CLASS Class has been corrupted.  
	*	- MORPHOERR_FFD False Finger Detected.  
	*	- MORPHOERR_MOIST_FINGER The finger can be too moist or the scanner is wet.  
	*	- MORPHOERR_LICENSE_MISSING A required license is missing (MorphoSmart™; FINGER VP only). */
	public int getImage(
						int timeOut,
						int acquisitionThreshold,
						int compressAlgo,
						int compressRate,
						int detectModeChoice,
						int latentDetection,
						MorphoImage morphoImage,
						int callbackCmd,
						Observer callback
						)
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		return morphoDeviceNative.getImage(morphoDevicePointerCPP, timeOut, acquisitionThreshold, compressAlgo, compressRate, detectModeChoice, latentDetection, morphoImage, callbackCmd, callback);
	}
	
	/** This function retrieves MorphoSmart™; configuration. 
	 * @param SecuConfig binary mask that indicates security options managed by the MorphoSmart™;. 
		- Binary mask is defined as follow:
		- b8 b7 b6 b5 b4 b3 b2 b1 Biometric Subtype Mask 
		- Mask = 0x00 No security options. 
		- b1 = 1 The MorphoSmart™; uses the "tunneling" protocol. 
		- b2 = 1 The MorphoSmart™; uses the "offered security" protocol. 
		- b3 = 1 The MorphoSmart™; uses only templates with a digital signature (within an X9.84 envelop). 
		- b4 = 1 The MorphoSmart™; only accept signed firmware packages during retrofit. 
		- b5 = 1 The MorphoSmart™; can not return the matching score. 
		- b6-b7-b8 RFU 
	@return values:
		- MORPHO_OK The matching was successful.  
		- MORPHOERR_CORRUPTED_CLASS Class has been corrupted. 
*/
	public int getSecuConfig(SecuConfig secuConfig)
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		return morphoDeviceNative.getSecuConfig(morphoDevicePointerCPP, secuConfig);
	}
	
	/**This function requests the terminal to start the unlock process, and to return the value of the seed to be used for the next step of the unlock process. The unlocking process is detailed in the MorphoSmartHostSystemInterfaceSpecification.pdf document.
	 * @param None
	 * @return string seed */
	public String getUnlockSeed()
	{
		return morphoDeviceNative.getUnlockSeed(morphoDevicePointerCPP);
	}
	
	/** InitUsbDevicesNameEnum must be call before using this function. This function returns a string containing the name of the MSO.
	 * @param index This index is between 0 and the number of connected device returned by the function InitUsbDevicesNameEnum.  
	 * @return Return values:
		- MORPHO_OK The execution of the function was successful.  
		- MORPHOERR_BADPARAMETER The index is unknown. 
		*/
	public String getUsbDeviceName(int index)
	{
		return morphoDeviceNative.getUsbDeviceName(morphoDevicePointerCPP, index);
	}
	
	/**This function enumerates the number of connected USB MorphoSmart™; devices. It builds an internal list which is composed of USB MorphoSmart™; name and returns the number of plugged device. The MorphoSmart™; name is the product number concatenated with the serial number. To get the name of the device that are currently plugged to the PC, call the GetUsbDevicesNameEnum function.
	 * @param nbUsbDevice The number of the USB MorphoSmart™; devices plugged to the host.
	 * @return Return values:
		- MORPHO_OK The execution of the function was successful.  
		- MORPHOERR_INTERNAL USB errors  
	 */
	public int initUsbDevicesNameEnum(Integer nbUsbDevice)
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		
		USBManager.enumerate();
		
		int ret = USBManager.isPermissionGranted();
		
		if( ret == ErrorCodes.MORPHO_OK)
		{
			return morphoDeviceNative.initUsbDevicesNameEnum(morphoDevicePointerCPP, nbUsbDevice);
		}
		else
		{
			return ret;
		}
	}
	
	/**This function return the name of the connected MorphoSmart™ device.
	 * @param index This index is between 0 and the number of connected device returned by the function InitUsbDevicesNameEnum. 
	 * @return Return values:
		- MORPHO_OK The execution of the function was successful.  
		- MORPHOERR_BADPARAMETER The index is unknown.  
	 */
	
	public String getUsbDevicePropertie(int index)
	{
		return morphoDeviceNative.getUsbDevicePropertie(morphoDevicePointerCPP, index);
	}
	
	/** MORPHO USE ONLY */
	public boolean isCSEnabled()
	{
		return morphoDeviceNative.isCSEnabled(morphoDevicePointerCPP);
	}
	
	/** This method indicates if exported biometric data encryption is activated and returns diversification data. Exported biometric data encryption is enabled/disabled with EnableDataEncryption.
	 * @param None 
	 * @return values:
		- True Exported biometric data encryption is activated  
		- False Exported biometric data encryption is not activated  
	 */
	public boolean isDataEncryptionEnabled()
	{

		return morphoDeviceNative.isDataEncryptionEnabled(morphoDevicePointerCPP);
	}

	/** This function reboot the MorphoSmart™; condicion.
	 * @param None
	 * @return values:
		- MORPHO_OK The function was successful.  
		- MORPHOERR_INTERNAL An error occurred during the execution of the function. 
	*/
	public int rebootSoft()
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		return morphoDeviceNative.rebootSoft(morphoDevicePointerCPP);
	}
	
	/** This function unlocks the MorphoSmart™;. The host transmits the Seed retrieved by a prior call to the GetUnlockSeed function, ciphered with the Secret corresponding to the SecretID that it owns. These two parameters are given by Morpho to the customer, upon his request.
	 * 
	 * @param SecretID Contains the Identification of the Secret used to cipher the seed.  
	 * @param CipheredSeed Contains the ciphered seed by the Secret according to the SecretID. 
	 * 
	 * @return values:
		- MORPHO_OK The function was successful.  
		- MORPHOERR_INTERNAL An error occurred during the execution of the function.  
		- MORPHOERR_BADPARAMETER One or more input parameters are out of range.  
	 */
	public int unlock(String SecretID, String CipheredSeed)
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		return morphoDeviceNative.unlock(morphoDevicePointerCPP, SecretID, CipheredSeed);
	}
	
	/** This function captures a live finger and checks if it matches with the user referred to. The maximum number of reference templates is 20.
	 * @param timeOut Finger detection timeout in seconds. Its value must be between 0 and 0xFFFF. 0 corresponds to an infinite timeout.  
	 * @param far This parameter specifies how tight the matching threshold is. Morpho recommends MORPHO_FAR_5 (see "T_ MORPHO_FAR"). 
	 * @param coder contains the biometric coder to use (MORPHO_MSO_V9_CODER or MORPHO_MSO_V9_JUV_CODER). Morpho recommends using MORPHO_MSO_V9_CODER. Please refer to the MorphoSmartHostInterface document for details.  
	 * @param detectModeChoice Bitmask of the following:
		- MORPHO_VERIF_DETECT_MODE: more permissive mode than default; MorphoSmart™; detects more easily finger presence, but might issue lower quality templates. 
		- MORPHO_ENROLL_DETECT_MODE: strongest detection mode (default mode). 
		- MORPHO_WAKEUP_LED_OFF: (only available on MorphoSmart™; MSO FFD) leds are turned off while waiting for a finger (impedance wakeup). 
		- MORPHO_FORCE_FINGER_ON_TOP_DETECT_MODE: (not available on MorphoSmart™; CBM-V) force the finger to cover the top of the capture area to increase quality. 
		- MORPHO_WAKEUP_LED_ON: (only available on MorphoSmart™; FINGER VP) leds are turned on while waiting for a finger.  
	 * @param matchingStrategy Value among of the following:
		- MORPHO_STANDARD_MATCHING_STRATEGY: default strategy. 
		- MORPHO_ADVANCED_MATCHING_STRATEGY: less FRR, but more processing time (not available on MorphoSmart™; FINGER VP).
		
	 * @param templateList List of candidate's templates to match. The number of templates must be
	 * less or equal to 20. The template format should be MORPHO_PK_COMP.
	 * The other template formats (MORPHO_PK_COMP_NORM, or MORPHO_PK_MAT, or
	 * MORPHO_PK_MAT_NORM, MORPHO_PK_ANSI_378, MORPHO_PK_MINEX_A, MORPHO_PK_ISO_FMR,
	 * MORPHO_PK_ISO_FMC_NS or MORPHO_PK_ISO_FMC_CS) are also supported, but Morpho
	 * recommends using them only for compatibility with existing systems or specific usage.
	 * Only a secure MorphoSmart™; accept the X984 biometric token and verify the integrity.
	 * @param callbackCmd Binary mask with CallbackMask elements.
	 * @param callback User context called on the reception of asynchronous status. null if not used.
	 * @param resultMatching The result of matching represented by :
	 * 	- matchingScore
	 * 	- matchingPKNumber
	 * 
	 * @return values:
		- MORPHO_OK The matching was successful.  
		- MORPHOERR_INTERNAL An error occurred during the execution of the function.  
		- MORPHOERR_BADPARAMETER The matching threshold value or timeout value is out of range or there is no biometric input data.  
		- MORPHOERR_INVALID_TEMPLATE The reference template is not valid: bad identifier, corrupted minutiae.  
		- MORPHOERR_TIMEOUT The finger detection timeout has expired.  
		- MORPHOERR_NO_HIT The matching returned a No Hit.  
		- MORPHOERR_CMDE_ABORTED Command is canceled.  
		- MORPHOERR_NO_REGISTERED_TEMPLATE Template list is empty.  
		- MORPHOERR_MIXED_TEMPLATE Template list contains templates with mixed formats.  
		- MORPHOERR_MEMORY_PC Not enough memory on the PC.  
		- MORPHOERR_PROTOCOLE Communication protocol error.  
		- MORPHOERR_CORRUPTED_CLASS Class has been corrupted.  
		- MORPHOERR_FFD False Finger Detected.  
		- MORPHOERR_MOIST_FINGER The finger can be too moist or the scanner is wet.  
		- MORPHOERR_FVP_MINUTIAE_SECURITY_MISMATCH Fingerprint template cannot be matched in high security level (MorphoSmart™; FINGER VP only).  
		- MORPHOERR_LICENSE_MISSING A required license is missing (MorphoSmart™; FINGER VP only).  
		- MORPHOERR_FVP_FINGER_MISPLACED_OR_WITHDRAWN Finger was misplaced or has been withdrawn from sensor during acquisition (MorphoSmart™; FINGER VP only).  
		- MORPHOERR_FFD_FINGER_MISPLACED Finger was misplaced during acquisition (MorphoSmart™; MSO FFD only).  
	 */
	public int verify(
						int timeOut,
						int far,
						Coder coder,
						int detectModeChoice,
						int matchingStrategy,
						TemplateList templateList,
						int callbackCmd,
						Observer callback,
						ResultMatching resultMatching
						)
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		return morphoDeviceNative.verify(morphoDevicePointerCPP, timeOut, far, coder.getCode(), detectModeChoice, matchingStrategy, templateList, callbackCmd, callback, resultMatching);
	}
	
	/** This function checks if one template of the reference template list matches with the unique template of the search template list. The maximum number of reference templates is 20. The maximum number of search template is 1.
	 * @param far This parameter specifies how tight the matching threshold is. Morpho recommends MORPHO_FAR_5 (see paragraph "T_ MORPHO_FAR").  
	 * @param templateListSearch Search template list. This list must include one unique template. The template format should be MORPHO_PK_COMP. The other template formats (MORPHO_PK_COMP_NORM, MORPHO_PK_MAT, MORPHO_PK_MAT_NORM, MORPHO_PK_ANSI_378, MORPHO_PK_MINEX_A, MORPHO_PK_ISO_FMR, MORPHO_PK_ISO_FMC_NS or MORPHO_PK_ISO_FMC_CS) are also supported but Morpho recommends using them only for compatibility with existing systems or specific usage. Only secure MorphoSmart™; accepts the X984 biometric token and verify the integrity.  
	 * @param templateListReference Reference templates list. The number of templates must be lower than 20. The template format should be MORPHO_PK_COMP. The other template formats (MORPHO_PK_COMP_NORM, MORPHO_PK_MAT, MORPHO_PK_MAT_NORM, MORPHO_PK_ANSI_378, MORPHO_PK_MINEX_A, MORPHO_PK_ISO_FMR, MORPHO_PK_ISO_FMC_NS or MORPHO_PK_ISO_FMC_CS) are also supported but Morpho recommends using them only for compatibility with existing systems or specific usage. Only secure MorphoSmart™; accept the X984 biometric token and verify the integrity.  
	 * @param matchingScore Contains the result matching score. NULL if not used. For security reason, the secure MorphoSmart™; cannot export the matching score because a "rogue" application can mount a "hillclimbing" attack by sequentially randomly modifying a sample and retaining only the changes that produce an increase in the returned score.  
	 * @return values:
		- MORPHO_OK The function was successful.  
		- MORPHOERR_INTERNAL An error occurred during the execution of the function.  
		- MORPHOERR_BADPARAMETER The matching threshold value or timeout value is out of range or there is no biometric input data.  
		- MORPHOERR_INVALID_TEMPLATE One template is not valid: bad identifier, corrupted minutiae.  
		- MORPHOERR_NO_HIT There are no common fingers between the two users.  
		- MORPHOERR_CMDE_ABORTED Command is canceled.  
		- MORPHOERR_NO_REGISTERED_TEMPLATE At least one template list is empty.  
		- MORPHOERR_MIXED_TEMPLATE Template list contains templates with mixed formats.  
		- MORPHOERR_MEMORY_PC Not enough memory on the PC.  
		- MORPHOERR_PROTOCOLE Communication protocol error.  
		- MORPHOERR_LICENSE_MISSING A required license is missing (MorphoSmart™; FINGER VP only).  
		- MORPHOERR_INVALID_PK_FORMAT Invalid template format  
		- MORPHOERR_CORRUPTED_CLASS Class has been corrupted.  
	 */
	public int verifyMatch(
						int far,
						TemplateList templateListSearch,
						TemplateList templateListReference,
						Integer matchingScore
						)
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		return morphoDeviceNative.verifyMatch(morphoDevicePointerCPP, far, templateListSearch, templateListReference, matchingScore);
	}

	/**This function retrieves the KCV (key check value) of a key saved in the MorphoSmart™;.
	 * @param keyID Identifier of the key:
		- ID_KENC 
		- ID_KSECRET 
		- ID_KS  
	 * @return KCV */
	public String getKCV(int keyID)
	{
		if(!this.cppMemOwn)
		{
			return "Class not instantiated";
		}
		return morphoDeviceNative.getKCV(this.morphoDevicePointerCPP, keyID);
	}
	
	/**This method loads a new symmetric key KS in the MorphoSmart™; using the unsecure mode: the new key is sent to device in plaintext. This mode is only available for standard devices (i.e. not secure).
	 * @param key New symmetric key KS in plaintext.
	 * @return values:
		- MORPHO_OK  
		- MORPHOERR_BADPARAMETER  
		- MORPHOERR_INVALID_CLASS  
		- MORPHOERR_CORRUPTED_CLASS  
*/
	public int loadKs(byte[] key)
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		return morphoDeviceNative.loadKs(this.morphoDevicePointerCPP, key);
	}

	/** This function loads a new Key in the MorphoSmart™;, this key is used to ciffer the Template passed to the Smart Card in a Match on Card process It uses the secured key load solution 
	 * @param key_enc_Ciffered_by_Certificate Defines the Kenc Ciffered by the MSO Certificate.  
	 * @param key_enc_Ciffered_by_Certificate_Signature Defines the Signature of the previous parameter, the Kenc Ciffered by the MSO Certificate.  
	 * @param hostCertificate The host certificate encoded in DER format used for the signature.  
	 * @return values:
		- MORPHO_OK The execution of the function was successful.  
		- MORPHOERR_INVALID_CLASS The class has been destroyed  
		- MORPHOERR_CORRUPTED_CLASS Class has been corrupted.  
	 */
	public int loadMocKey(byte[] key_enc_Ciffered_by_Certificate, byte[] key_enc_Ciffered_by_Certificate_Signature, byte[] hostCertificate)
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		return morphoDeviceNative.loadMocKey(this.morphoDevicePointerCPP, key_enc_Ciffered_by_Certificate, key_enc_Ciffered_by_Certificate_Signature, hostCertificate);
	}
	
	
	/**
	 * This function loads a new Key in the MorphoSmart™, this key is used to ciffer the Template passed to the Smart Card in a Match on Card process
	 *	It uses the compatible key load solution
	 * @param key_enc_Ciffered_by_KencTrans the key Kenc ciffered by KencTrans used to ciffer the Template
	 * @return values :	 
	 * 	- MORPHO_OK	The execution of the function was successful.
	 * 	- MORPHOERR_INVALID_CLASS	The class has been destroyed
	 *	- MORPHOERR_CORRUPTED_CLASS	Class has been corrupted.
	 */

	public int loadMocKey(byte[] key_enc_Ciffered_by_KencTrans)
	{
		if(!this.cppMemOwn)
		{
			return ErrorCodes.CLASS_NOT_INSTANTIATED;
		}
		return morphoDeviceNative.loadMocKey(this.morphoDevicePointerCPP, key_enc_Ciffered_by_KencTrans);
	}
}

/**
 * Corresponding native class of MorphoDevice.
 */
class MorphoDeviceNative
{	
	static {
		System.loadLibrary("NativeMorphoSmartSDK");
	}
	
	public native long getCPPInstance();
	
	public native int releaseFFDLogs(long morphoDevicePointerCPP, String log) ;

	public native String getKCV(long morphoDevicePointerCPP, int keyID) ;

	public native long getCPPInstance(long morphoDevicePointerCPP);
	
	public native void deleteInstance(long morphoDevicePointerCPP);
	
	public native int getDatabase(long morphoDevicePointerCPP,int databaseIndex, MorphoDatabase morphoDatabase);
	
	public native int cancelLiveAcquisition(long morphoDevicePointerCPP);

	public native int capture(
						long morphoDevicePointerCPP,
						int timeout,
						int acquisitionThreshold,
						int advancedSecurityLevelsRequired,
						int fingerNumber,
						int templateType,
						int templateFVPType,
						int maxSizeTemplate,
						int enrollType,
						int latentDetection,
						int coderChoice,
						int detectModeChoice,
						TemplateList templateList,
						int callbackCmd,
						Observer callback
						);
	
	public native int closeDevice(long morphoDevicePointerCPP);
	
	public native int enableCS(long morphoDevicePointerCPP,boolean enable);
	
	public native int enableDataEncryption(long morphoDevicePointerCPP,boolean enable, String diversificationData);
	
	public native int openDevice(long morphoDevicePointerCPP,int serialPortNumber, int baudRate);
					
	public native String getProductDescriptor(long morphoDevicePointerCPP);
	
	public native String getSensorDescriptor(long morphoDevicePointerCPP);
	
	public native String getSoftwareDescriptor(long morphoDevicePointerCPP);
	
	public native int openUsbDevice(long morphoDevicePointerCPP,String sensorName,int timeOut);
	
	public native int setConfigParam(long morphoDevicePointerCPP,int tag,String paramValue);
	
	public native int setSecurityLevel(long morphoDevicePointerCPP,int securityLevel);
	
	public native int getSecurityLevel(long morphoDevicePointerCPP);
	
	public native int getComType(long morphoDevicePointerCPP);
	
	public native String getConfigParam(long morphoDevicePointerCPP,int parameterIdentifier);
	
	public native String getStringDescriptorBin(long morphoDevicePointerCPP,int descriptorIdentifier);
	
	public native int getIntDescriptorBin(long morphoDevicePointerCPP,int descriptorIdentifier);
	
	public native String getFFDLogs(long morphoDevicePointerCPP);
	
	public native int getInternalError(long morphoDevicePointerCPP);
	
	public native int getImage(
						long morphoDevicePointerCPP,
						int timeOut,
						int acquisitionThreshold,
						int compressAlgo,
						int compressRate,
						int detectModeChoice,
						int latentDetection,
						MorphoImage morphoImage,
						int callbackCmd,
						Observer callback
						);
	
	public native int getSecuConfig(long morphoDevicePointerCPP,SecuConfig secuConfig);
	
	public native String getUnlockSeed(long morphoDevicePointerCPP);
	
	public native int initUsbDevicesNameEnum(long morphoDevicePointerCPP,Integer nbUsbDevice);
	
	public native String getUsbDeviceName(long morphoDevicePointerCPP,int index);
	
	public native String getUsbDevicePropertie(long morphoDevicePointerCPP,int index);
	
	public native boolean isCSEnabled(long morphoDevicePointerCPP);
	
	public native boolean isDataEncryptionEnabled(long morphoDevicePointerCPP);

	public native int rebootSoft(long morphoDevicePointerCPP);
	
	public native int unlock(long morphoDevicePointerCPP,String SecretID, String CipheredSeed);
	
	public native int verify(
						long morphoDevicePointerCPP,
						int timeOut,
						int far,
						int coderChoice,
						int detectModeChoice,
						int matchingStrategy,
						TemplateList templateList,
						int callbackCmd,
						Observer callback,
						ResultMatching resultMatching
						);
	
	public native int verifyMatch(
						long morphoDevicePointerCPP,
						int far,
						TemplateList templateListSearch,
						TemplateList templateListReference,
						Integer matchingScore
						);
 
	public native int loadKs(long morphoDevicePointerCPP, byte[] key);

	public native int loadMocKey(long morphoDevicePointerCPP, byte[] key_enc_Ciffered_by_Certificate, byte[] key_enc_Ciffered_by_Certificate_Signature, byte[] hostCertificate);

	public native int loadMocKey(long morphoDevicePointerCPP, byte[] key_enc_Ciffered_by_KencTrans);
}
