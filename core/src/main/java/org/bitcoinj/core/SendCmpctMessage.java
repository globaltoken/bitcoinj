package org.bitcoinj.core;

public class SendCmpctMessage extends EmptyMessage {
    public SendCmpctMessage() {
    }

    // this is needed by the BitcoinSerializer
    public SendCmpctMessage(NetworkParameters params, byte[] payload) {
    }
}