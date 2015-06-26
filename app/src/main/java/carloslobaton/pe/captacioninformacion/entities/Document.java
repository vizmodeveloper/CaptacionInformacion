package carloslobaton.pe.captacioninformacion.entities;

import android.util.Log;

/**
 * Created by DanielRolando on 26/06/2015.
 */
public class Document {
    private String idVersion;
    private String dni;
    private String fLastName;
    private String mLastName;
    private String names;
    private String gender;
    private String codeLeftFingerprint;
    private String templateLeftFingerprint;
    private String whiteBytes1;
    private String codeRightFingerprint;
    private String templateRightFingerprint;
    private String whiteBytes2;
    private String expirationDate;
    private String formNumber;
    private String photoNumber;

    public Document(String documentInfo){
        //documentInfo ="0145462875SANCHEZ                                 VELARDE                                 DANIEL ROLANDO                     102Q%-6@sEGTWX\\]_cejqnttuvxwxjytg{BofZ&*t$fy[nfhWn3cQE3$407Q)v.u./4h;;>|@BGIaKkMsPRY_[_adykem\\tly{}||cSoje@uIF@202103235969834059698340";
        idVersion = documentInfo.substring(0,2);
        dni = documentInfo.substring(2,10);
        fLastName = documentInfo.substring(10,50).trim();
        mLastName = documentInfo.substring(50,90).trim();
        names = documentInfo.substring(90,125).trim();
        gender = documentInfo.substring(125,126);
//        codeLeftFingerprint = documentInfo.substring(126,128);
//        templateLeftFingerprint = documentInfo.substring(128,284);
//        whiteBytes1 = documentInfo.substring(284,287);
//        codeRightFingerprint = documentInfo.substring(287,289);
//        templateRightFingerprint = documentInfo.substring(289,445);
//        whiteBytes2 = documentInfo.substring(445,448);
        codeRightFingerprint = documentInfo.substring(126,128);
        templateRightFingerprint = documentInfo.substring(128,236);
        expirationDate = documentInfo.substring(236,244);
        formNumber = documentInfo.substring(244,252);
        photoNumber = documentInfo.substring(252,260);
















        Log.d("INFO",documentInfo);

    }

    public String getIdVersion() {
        return idVersion;
    }

    public void setIdVersion(String idVersion) {
        this.idVersion = idVersion;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getfLastName() {
        return fLastName;
    }

    public void setfLastName(String fLastName) {
        this.fLastName = fLastName;
    }

    public String getmLastName() {
        return mLastName;
    }

    public void setmLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCodeLeftFingerprint() {
        return codeLeftFingerprint;
    }

    public void setCodeLeftFingerprint(String codeLeftFingerprint) {
        this.codeLeftFingerprint = codeLeftFingerprint;
    }

    public String getTemplateLeftFingerprint() {
        return templateLeftFingerprint;
    }

    public void setTemplateLeftFingerprint(String templateLeftFingerprint) {
        this.templateLeftFingerprint = templateLeftFingerprint;
    }

    public String getWhiteBytes1() {
        return whiteBytes1;
    }

    public void setWhiteBytes1(String whiteBytes1) {
        this.whiteBytes1 = whiteBytes1;
    }

    public String getCodeRightFingerprint() {
        return codeRightFingerprint;
    }

    public void setCodeRightFingerprint(String codeRightFingerprint) {
        this.codeRightFingerprint = codeRightFingerprint;
    }

    public String getTemplateRightFingerprint() {
        return templateRightFingerprint;
    }

    public void setTemplateRightFingerprint(String templateRightFingerprint) {
        this.templateRightFingerprint = templateRightFingerprint;
    }

    public String getWhiteBytes2() {
        return whiteBytes2;
    }

    public void setWhiteBytes2(String whiteBytes2) {
        this.whiteBytes2 = whiteBytes2;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getFormNumber() {
        return formNumber;
    }

    public void setFormNumber(String formNumber) {
        this.formNumber = formNumber;
    }

    public String getPhotoNumber() {
        return photoNumber;
    }

    public void setPhotoNumber(String photoNumber) {
        this.photoNumber = photoNumber;
    }
}
