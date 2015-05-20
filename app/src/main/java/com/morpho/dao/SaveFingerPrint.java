/**
 * 
 */
package com.morpho.dao;

/**
 * @author neha Shukla
 * 
 */

public class SaveFingerPrint {

	private byte[] fpPrev = null;
	private String fplevel = null;

	public byte[] getFpPrev() {
		return fpPrev;
	}

	/**
	 * @return the fplevel
	 */
	public String getFplevel() {
		return fplevel;
	}

	/**
	 * @param fplevel
	 *            the fplevel to set
	 */
	public void setFplevel(String fplevel) {
		this.fplevel = fplevel;
	}

	/**
	 * @return the mSaveFingerPrint
	 */
	public static SaveFingerPrint getmSaveFingerPrint() {
		return mSaveFingerPrint;
	}

	/**
	 * @param mSaveFingerPrint
	 *            the mSaveFingerPrint to set
	 */
	public static void setmSaveFingerPrint(SaveFingerPrint mSaveFingerPrint) {
		SaveFingerPrint.mSaveFingerPrint = mSaveFingerPrint;
	}

	public void setFpPrev(byte[] fpPrev) {
		this.fpPrev = fpPrev;
	}

	private static SaveFingerPrint mSaveFingerPrint = new SaveFingerPrint();

	public static SaveFingerPrint getInstance() {
		return mSaveFingerPrint;
	}
	
	
}
