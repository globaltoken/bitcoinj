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
        checkpoints.put(50000, Sha256Hash.wrap("0000000000024f4cefcf1bcbe62287f8292e6ff2eb10b1761de4134b93e429d5"));
        checkpoints.put(50010, Sha256Hash.wrap("0000000000063d8718265e703fe003d2219adc369ce39eb6a1c7ddcbea6a95ee"));
        checkpoints.put(50020, Sha256Hash.wrap("00000000000296ad876b5fe0bbfe9b00f9d1691a896c20262666d1175eb0c492"));
        checkpoints.put(50050, Sha256Hash.wrap("000000000005fa1d787edb692b6cd844305fee830a98f92fddf4edef983a410d"));
        checkpoints.put(50100, Sha256Hash.wrap("000000000004af13462e8153ee9adfacde7fb2ecd28b0fb48bcec1e4240a792e"));
		checkpoints.put(80000, Sha256Hash.wrap("000000000000c1f8a5bddc42d82b6bdd322afdba3afa95670c7e4aa5073594e6"));

        dnsSeeds = new String[] {
                "134.255.221.7",         // Globaltoken Base Node
				"75.109.15.26",          // Globaltoken Node 1
				"185.188.6.41",          // Globaltoken Node 2
				"78.46.198.89",          // Globaltoken Node 3
				"5.9.39.9"               // Globaltoken Node 4
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