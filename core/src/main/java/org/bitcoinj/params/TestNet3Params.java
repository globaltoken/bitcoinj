/*
 * Copyright 2013 Google Inc.
 * Copyright 2014 Andreas Schildbach
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

import java.math.BigInteger;
import java.util.Date;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.Utils;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;

import static com.google.common.base.Preconditions.checkState;

/**
 * Parameters for the testnet, a separate public instance of Bitcoin that has relaxed rules suitable for development
 * and testing of applications and new Bitcoin versions.
 */
public class TestNet3Params extends AbstractBitcoinNetParams {
    public static final int TESTNET_MAJORITY_WINDOW = 100;
    public static final int TESTNET_MAJORITY_REJECT_BLOCK_OUTDATED = 75;
    public static final int TESTNET_MAJORITY_ENFORCE_BLOCK_UPGRADE = 51;

    public TestNet3Params() {
        super();
        id = ID_TESTNET;
        packetMagic = 0x3a6f375b;

        interval = INTERVAL;
        targetTimespan = TARGET_TIMESPAN;
        maxTarget = Utils.decodeCompactBits(0x1d00ffffL);
        port = 19319;
        addressHeader = 111;
        p2shHeader = 196;
        dumpedPrivateKeyHeader = 239;
        segwitAddressHrp = "tg";
        genesisBlock.setTime(1480961109L);
        genesisBlock.setDifficultyTarget(0x1d00ffffL);
        genesisBlock.setNonce(2864352084L);
        spendableCoinbaseDepth = 100;
        subsidyDecreaseBlockCount = 840000;
        String genesisHash = genesisBlock.getHashAsString();
        checkState(genesisHash.equals("00000000fe3e3e93344a6b73888137397413eb11f601b4231b5196390d24d3b6"));
        alertSigningKey = Utils.HEX.decode("042cddcb679f0089cac93820d4e7e309a43e0bb7d02e9750c9e8dacaca58bd98662eba63834eff2efbb62da1aa51af4c7885d68fdb4f020a37909dff4c1467d28f");

        dnsSeeds = new String[] {
            "134.255.221.7", // Globaltoken base node
            "bchain.info", // bchain.info
            "globaltoken.org", // GlobalToken base node II
            "explorer.globaltoken.org", // GlobalToken base node III
            "lameserver.de", // GlobalToken Node by Astrali
            "pool.cryptopowered.club",  // GlobalToken Cryptopowered node
            "pool2.cryptopowered.club", // GlobalToken Cryptopowered node
            "bit2pool.com", // GlobalToken Bit2Pool node
            "185.206.144.200", // GlobalToken Hardfork node: 01/18
            "185.206.145.201", // GlobalToken Hardfork node: 02/18
            "185.206.146.200", // GlobalToken Hardfork node: 03/18
            "185.206.147.203", // GlobalToken Hardfork node: 04/18
            "185.205.209.67",  // GlobalToken Hardfork node: 05/18
            "185.206.147.202", // GlobalToken Hardfork node: 06/18
            "185.205.209.137", // GlobalToken Hardfork node: 07/18
            "185.203.119.194", // GlobalToken Hardfork node: 08/18
            "185.203.119.195", // GlobalToken Hardfork node: 09/18
            "185.206.144.201", // GlobalToken Hardfork node: 10/18
            "185.141.62.86",   // GlobalToken Hardfork node: 11/18
            "185.141.62.87",   // GlobalToken Hardfork node: 12/18
            "185.141.62.88",   // GlobalToken Hardfork node: 13/18
            "185.141.62.89",   // GlobalToken Hardfork node: 14/18
            "185.141.62.90",   // GlobalToken Hardfork node: 15/18
            "185.141.62.91",   // GlobalToken Hardfork node: 16/18
            "185.141.62.92",   // GlobalToken Hardfork node: 17/18
            "185.203.118.117", // GlobalToken Hardfork node: 18/18
        };
        addrSeeds = null;
        bip32HeaderPub = 0x043587CF;
        bip32HeaderPriv = 0x04358394;

        majorityEnforceBlockUpgrade = TESTNET_MAJORITY_ENFORCE_BLOCK_UPGRADE;
        majorityRejectBlockOutdated = TESTNET_MAJORITY_REJECT_BLOCK_OUTDATED;
        majorityWindow = TESTNET_MAJORITY_WINDOW;
    }

    private static TestNet3Params instance;
    public static synchronized TestNet3Params get() {
        if (instance == null) {
            instance = new TestNet3Params();
        }
        return instance;
    }

    @Override
    public String getPaymentProtocolId() {
        return PAYMENT_PROTOCOL_ID_TESTNET;
    }

    // February 16th 2012
    private static final Date testnetDiffDate = new Date(1329264000000L);

    @Override
    public void checkDifficultyTransitions(final StoredBlock storedPrev, final Block nextBlock,
        final BlockStore blockStore) throws VerificationException, BlockStoreException {
        if (!isDifficultyTransitionPoint(storedPrev.getHeight()) && nextBlock.getTime().after(testnetDiffDate)) {
            Block prev = storedPrev.getHeader();

            // After 15th February 2012 the rules on the testnet change to avoid people running up the difficulty
            // and then leaving, making it too hard to mine a block. On non-difficulty transition points, easy
            // blocks are allowed if there has been a span of 20 minutes without one.
            final long timeDelta = nextBlock.getTimeSeconds() - prev.getTimeSeconds();
            // There is an integer underflow bug in bitcoin-qt that means mindiff blocks are accepted when time
            // goes backwards.
            if (timeDelta >= 0 && timeDelta <= NetworkParameters.TARGET_SPACING * 2) {
        	// Walk backwards until we find a block that doesn't have the easiest proof of work, then check
        	// that difficulty is equal to that one.
        	StoredBlock cursor = storedPrev;
        	while (!cursor.getHeader().equals(getGenesisBlock()) &&
                       cursor.getHeight() % getInterval() != 0 &&
                       cursor.getHeader().getDifficultyTargetAsInteger().equals(getMaxTarget()))
                    cursor = cursor.getPrev(blockStore);
        	BigInteger cursorTarget = cursor.getHeader().getDifficultyTargetAsInteger();
        	BigInteger newTarget = nextBlock.getDifficultyTargetAsInteger();
        	if (!cursorTarget.equals(newTarget))
                    throw new VerificationException("Testnet block transition that is not allowed: " +
                	Long.toHexString(cursor.getHeader().getDifficultyTarget()) + " vs " +
                	Long.toHexString(nextBlock.getDifficultyTarget()));
            }
        } else {
            super.checkDifficultyTransitions(storedPrev, nextBlock, blockStore);
        }
    }
}