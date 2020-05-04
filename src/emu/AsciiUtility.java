package emu;

import java.util.ArrayList;
import java.util.Arrays;

public class AsciiUtility {
    private static final ControlCharacter[] CONTROL_CHARACTERS = new ControlCharacter[]{
            new ControlCharacter("SOH", (byte) 0x01),
            new ControlCharacter("STX", (byte) 0x02),
            new ControlCharacter("ETX", (byte) 0x03),
            new ControlCharacter("ACK", (byte) 0x06),
            new ControlCharacter("LF", (byte) 0x0A),
            new ControlCharacter("CR", (byte) 0x0D)
    };

    /**
     * Checks if a code snippet is a ASCII control character
     *
     * @param ascii ASCII code snippet
     * @return true if the the code snippet is a ASCII control character
     */
    public static boolean isControlCharacter(String ascii) {
        for (ControlCharacter c : CONTROL_CHARACTERS) {
            if (ascii.equals(c.controlCode)) return true;
        }
        return false;
    }

    /**
     * Converts an ASCII string to a list of its representative bytes
     *
     * @param ascii string with ASCII codes
     * @return list of bytes
     */
    public static byte[] convertAsciiToHexadecimal(String ascii, String delimiter) {
        String[] codeSnippets = ascii.split(delimiter);
        ArrayList<Byte> bytes = new ArrayList<>();

        boolean isControlCharacter;
        for (String codeSnippet : codeSnippets) {
            // Check if control character
            isControlCharacter = false;
            for (ControlCharacter controlCharacter : CONTROL_CHARACTERS) {
                if (codeSnippet.equals(controlCharacter.controlCode)) {
                    bytes.add(controlCharacter.hexCode);
                    isControlCharacter = true;
                }
            }

            if (!isControlCharacter) {
                // Split to characters and default encoding
                char[] charArray = codeSnippet.toCharArray();
                for (char c : charArray) {
                    bytes.add((byte) c);
                }
            }
        }

        // Convert to array
        byte[] byteArray = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            byteArray[i] = bytes.get(i);
        }

        return byteArray;
    }
}
