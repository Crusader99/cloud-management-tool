package de.hsaalen.cmt.crypto

/**
 * Interface for objects that can be encrypted or decrypted.
 */
interface Encryptable<T> {

    /**
     * Encrypt sensible information using personal session key and return new encrypted instance.
     */
    fun encrypt(): T

    /**
     * Decrypt sensible information using personal session key and return new decrypted instance.
     */
    fun decrypt(): T

}
