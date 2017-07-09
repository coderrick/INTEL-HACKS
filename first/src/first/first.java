//define package name.
package first;

//import using java card API interface.
import javacard.framework.*;


/* 
 * Package: first
 * Filename: first.java 
 * Class: first 
 */
public class first extends Applet
{
   //Define the value of CLA/INS in APDU, you can also define P1, P2.
   private static final byte CLA_DEMO_TEST            = (byte)0xB0;
    private static final byte INS_DEMO_TMP1            = (byte)0x11;
    private static final byte INS_DEMO_TMP2            = (byte)0x12;
    
   //Create an instance of the Applet subclass using its constructor, and to register the instance.
   public static void install(byte[] bArray, short bOffset, byte bLength) 
   {
      //Only one applet instance can be successfully registered each time the JCRE calls the Applet.install method.
      new first().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
   }
   //Process the command APDU, All APDUs are received by the JCRE and preprocessed. 
   public void process(APDU apdu)
   {
      //Select the Applet, through the select method, this applet is selectable, After successful selection, all APDUs are delivered to the currently selected applet via the process method.
      if (selectingApplet())
      {
         return;
      }
      //Get the APDU buffer byte array.
      byte[] buf = apdu.getBuffer();
      //Calling this method indicates that this APDU has incoming data. 
      apdu.setIncomingAndReceive();
      
      //If the CLA is not equal to 0xB0(CLA_DEMO_TEST),  throw an exception.
      if(buf[ISO7816.OFFSET_CLA] != CLA_DEMO_TEST)
      {
         ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
      }
      //Dispatch INS in APDU.
      switch (buf[ISO7816.OFFSET_INS])
      {
         //The APDU format can be "B0 11 P1 P2 Lc Data Le", such as "B0110000" or "B01101020311223300".
      case INS_DEMO_TMP1:
         SendData(apdu);
         break;
         //The APDU format can be "B0 12 P1 P2 Lc Data Le", such as "B0125566" or "B012000002112200".
      case INS_DEMO_TMP2:
         //Set 0x5555 into the 'buf' array,  the offset is 0.
         Util.setShort(buf, (short)0, (short)0x5555);
         //Send the first 2 bytes data in 'buf', the hex of JCRE sending data is "55 55 90 00".
         apdu.setOutgoingAndSend((short)0, (short)2);
         break;
      default:
         //If you don't know the INS, throw an exception.
         ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
      }
   }
   
   //Define a function named 'SendData'
   private void SendData(APDU apdu) 
   {
      //Get the apdu buffer datas again.
      byte [] buffer = apdu.getBuffer();
      
      //Define a byte string with the message "Hello World"
      byte sendStr[] = {'H','E','L','L','O',' ','W','O','R','L','D'};
      short len = (short) sendStr.length;
      //Copy "Hello world" character to APDU Buffer.
      Util.arrayCopyNonAtomic(sendStr, (short)0, buffer, (short)0, (short)len);
         
      //Set the data transfer direction to outbound.
      apdu.setOutgoing();
      //Set the actual length of response data.
      apdu.setOutgoingLength(len);
      //Sends the data of APDU buffer 'buf', the length is 'len' bytes,  the offset is 0.
      apdu.sendBytes((short) 0, (short)len);
   }

}
