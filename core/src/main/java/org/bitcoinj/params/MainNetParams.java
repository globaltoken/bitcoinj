/*
 * Copyright 2013 Google Inc.
 * Copyright 2015 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bitcoinj.params;

import org.bitcoinj.core.*;
import org.bitcoinj.net.discovery.*;

import java.net.*;

import static com.google.common.base.Preconditions.*;

/**
 * Parameters for the main production network on which people trade goods and services.
 */
public class MainNetParams extends AbstractBitcoinNetParams {
    public static final int MAINNET_MAJORITY_WINDOW = 1000;
    public static final int MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED = 950;
    public static final int MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE = 750;

    public MainNetParams() {
        super();
        interval = INTERVAL;
        targetTimespan = TARGET_TIMESPAN;
        maxTarget = Utils.decodeCompactBits(0x1d00ffffL);
        dumpedPrivateKeyHeader = 166;
        addressHeader = 38;
        p2shHeader = 5;
        acceptableAddressCodes = new int[] { addressHeader, p2shHeader };
        port = 9319;
        packetMagic = 0xc708d32dL;
        bip32HeaderPub = 0x0488B21E; //The 4 byte header that serializes in base58 to "xpub".
        bip32HeaderPriv = 0x0488ADE4; //The 4 byte header that serializes in base58 to "xprv"

        majorityEnforceBlockUpgrade = MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE;
        majorityRejectBlockOutdated = MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED;
        majorityWindow = MAINNET_MAJORITY_WINDOW;

        genesisBlock.setDifficultyTarget(0x1d00ffffL);
        genesisBlock.setTime(1480961109L);
        genesisBlock.setNonce(2864352084L);
        id = ID_MAINNET;
        subsidyDecreaseBlockCount = 840000;
        spendableCoinbaseDepth = 100;
        String genesisHash = genesisBlock.getHashAsString();
        checkState(genesisHash.equals("00000000fe3e3e93344a6b73888137397413eb11f601b4231b5196390d24d3b6"),
                genesisHash);

        // This contains (at a minimum) the blocks which are not BIP30 compliant. BIP30 changed how duplicate
        // transactions are handled. Duplicated transactions could occur in the case where a coinbase had the same
        // extraNonce and the same outputs but appeared at different heights, and greatly complicated re-org handling.
        // Having these here simplifies block connection logic considerably.
        checkpoints.put(500, Sha256Hash.wrap("00000000000018079c07b36a74919748129bbd3840de0a4bee720fa92fe2ad7a"));
		checkpoints.put(30000, Sha256Hash.wrap("00000000000086d8aa795fcd1d5f0db232c4a9d2c33ca8b976d06fd9c205c980"));
		checkpoints.put(80000, Sha256Hash.wrap("000000000000c1f8a5bddc42d82b6bdd322afdba3afa95670c7e4aa5073594e6"));
		checkpoints.put(125000, Sha256Hash.wrap("0000000000000d76f0e23774c3e181da3db141857df3c6bba8bd7b8c94e87cd0"));
		checkpoints.put(155555, Sha256Hash.wrap("000000000000855b551daf4e7758e53fd767781b652c71bb3bba1d340994cb8e"));
		checkpoints.put(190000, Sha256Hash.wrap("00000000000004518733e0c572ee08227577260345c1484921752c948009d238"));
		checkpoints.put(210000, Sha256Hash.wrap("00000000000001b9719d694b2c6b2a74173d9333ba7e629439d8de8bd42e993f"));
		
		
        dnsSeeds = new String[] {
                "134.255.221.7",
				"5.1.81.81",
				"149.56.241.2",
				"185.188.6.41",
				"62.75.171.246"
        };
        httpSeeds = null;

        addrSeeds = null;
    }

    private static MainNetParams instance;
    public static synchronized MainNetParams get() {
        if (instance == null) {
            instance = new MainNetParams();
        }
        return instance;
    }

    @Override
    public String getPaymentProtocolId() {
        return PAYMENT_PROTOCOL_ID_MAINNET;
    }
}