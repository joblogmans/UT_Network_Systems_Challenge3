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

        // max number of packet in row to send
        int maxPackets = 7;
        // get client ID as local variable, two digit variable. This is generated in Program as this remains static throughout.
        int ID = Program.clientID;
        // counter for number of successfully sent packets in a row.
        int sentPackets = 0;
        // control information is sent as state * 1000 + sentPackets * 100 + ID, so for example with id=29 and state 2 and 3 packets:
        // controlInformation = 2239
        // which can then be parsed.

        // parse controlInformation to be 8 decimals long with leading zeros as a string, e.g. 00001258
        String controlInformationString = String.format("%08d", controlInformation);
        // gets last two digits which is id
        int controlInformationID = controlInformation % 100;
        // get number of send packets which is third to last digit
        int controlInformationPackets = Integer.parseInt(controlInformationString.substring(5, 6));
        // gets queue of previous sender (first 5 digits)
        int state = Integer.parseInt(controlInformationString.substring(0, 5));

        if (controlInformationID == ID) {
            sentPackets = controlInformationPackets;
        }

        // DEBUG LINE
        // System.out.println("State: " + state + ". ID: " + controlInformationID + ". MyID: " + ID);

        // No data to send, just be quiet
        if (localQueueLength == 0) {
            System.out.println("NO QUEUE - No data to send.");
            return new TransmissionInfo(TransmissionType.Silent, ID);
        }

        // new control information to send (subtract one from queue so others know how much is left and add one successful packet)
        int newControlInformation = (localQueueLength-1)*100 + (sentPackets+1)*1000 + ID;

        if (previousMediumState != MediumState.Collision) { // No collision, if collision there are different rules
            if (state > 0 && controlInformationPackets < maxPackets && controlInformationID != ID) { // someone has a queue and it's not me. However, I will send if he reached max packets in a row
                System.out.println("WAIT - Queue: " + state + ", wait for node " + controlInformationID + " to finish.");
                return new TransmissionInfo(TransmissionType.Silent, ID);
            } else { // Other guy is done, not allowed to continue or no-one is sending, my turn to try.
                System.out.println("SEND - Queue: " + localQueueLength + ", sending");
                return new TransmissionInfo(TransmissionType.Data, newControlInformation);
            }
        } else {
            // Collision, xx% chance of sending
            if (new Random().nextInt(100) < 30) {
                // Continue as before
                System.out.println("COLL - Sending data after previous collision.");
                return new TransmissionInfo(TransmissionType.Data, newControlInformation);
            } else {
                System.out.println("COLL - Not sending data after collision.");
                return new TransmissionInfo(TransmissionType.Silent, ID);
            }
        }

    }

}
