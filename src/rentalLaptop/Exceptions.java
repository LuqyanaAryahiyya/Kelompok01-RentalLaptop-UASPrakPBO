package com.rentallaptop;

class LoginGagalException extends Exception{

    //membuat instance dengan pesan kesalahan
    public LoginGagalException(String message){
        super(message);
    }

    //membuat instance dengan pesan kesalahan dan penyebab awal
    public LoginGagalException(String message, Throwable cause){
        super(message, cause);
    }
}

class InputTidakValidException extends Exception{
    
    public InputTidakValidException(String message){
        super(message);
    }

    public InputTidakValidException(String message, Throwable cause) {
        super(message, cause);
    }
}

class BarangTidakTersediaException extends Exception {

    public BarangTidakTersediaException(String message) {
        super(message);
    }

    public BarangTidakTersediaException(String message, Throwable cause) {
        super(message, cause);
    }
}

class BarangTidakDitemukanException extends Exception {

    public BarangTidakDitemukanException(String message) {
        super(message);
    }

    public BarangTidakDitemukanException(String message, Throwable cause) {
        super(message, cause);
    }
}
