package my_protocol;

import framework.IMACProtocol;
import framework.MediumState;
import framework.TransmissionInfo;
import framework.TransmissionType;

import java.util.Random;

/**
 * A fairly trivial Medium Access Control scheme.
 *
 * @author Jaco ter Braak, University of Twente
 * @version 05-12-2013
 *
 * Copyright University of Twente,  2013-2019
 *
 **************************************************************************
 *                            Copyright notice                            *
 *                                                                        *
 *            This file may  ONLY  be distributed UNMODIFIED.             *
 * In particular, a correct solution to the challenge must NOT be posted  *
 * in public places, to preserve the learning effect for future students. *
 **************************************************************************
 */
public class MyProtocol implements IMACProtocol {

    @Override
    public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
                                              int controlInformation, int localQueueLength) {
        // No data to send, just be quiet
        if (localQueueLength == 0) {
            System.out.println("SLOT - No data to send.");
            return new TransmissionInfo(TransmissionType.Silent, 0);
        }

        if (previousMediumState == MediumState.Collision) {
            // Collision, 50% chance of sending
            if (new Random().nextInt(100) < 50) {
                System.out.println("COLISSION SLOT - Sending data after previous collision.");
                return new TransmissionInfo(TransmissionType.Data, 0);
            } else {
                System.out.println("COLLISION SLOT - Not sending data after collision.");
                return new TransmissionInfo(TransmissionType.Silent, 0);
            }
        } else {
            // No collision, randomly transmit with 60% probability
            if (new Random().nextInt(100) < 37) {
                System.out.println("SLOT - Sending data and hope for no collision.");
                return new TransmissionInfo(TransmissionType.Data, 0);
            } else {
                System.out.println("SLOT - Not sending data to give room for others.");
                return new TransmissionInfo(TransmissionType.Silent, 0);
            }
        }

    }

}
