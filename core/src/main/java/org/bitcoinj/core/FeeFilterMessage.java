package org.bitcoinj.core;

public class FeeFilterMessage extends EmptyMessage {
    public FeeFilterMessage() {
    }

    // this is needed by the BitcoinSerializer
    public FeeFilterMessage(NetworkParameters params, byte[] payload) {
    }
}