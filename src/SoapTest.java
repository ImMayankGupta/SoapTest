import java.util.Iterator;
 
import javax.xml.soap.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
 
import org.w3c.dom.Node;
 
public class SoapTest
{
 
    public static void main (String args[])
    {
        String arg1 = args[0];
        String arg2 = args[1];
 
        String operation = "add";    // could also be "subtract"
        String urn = "CalculatorService";
        String destination = "http://localhost:8080/axis/Calculator.jws";
 
        try
        {
            // First create the connection
            SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = soapConnFactory.createConnection();
 
            // Next, create the actual message
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage message = messageFactory.createMessage();
 
            SOAPPart soapPart = message.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();
 
            // This method demonstrates how to set HTTP and SOAP headers.
            // setOptionalHeaders(message, envelope);
 
            // Create and populate the body
            SOAPBody body = envelope.getBody();
 
            // Create the main element and namespace
            SOAPElement bodyElement = body.addChildElement(
                    envelope.createName(operation, "ns1", "urn:"+urn));
            // Add parameters
            bodyElement.addChildElement("in0").addTextNode(arg1);
            bodyElement.addChildElement("in1").addTextNode(arg2);
 
            // Save the message
            message.saveChanges();
 
            // Check the input
            /*
            System.out.println("\nRequest:\n");
            message.writeTo(System.out);
            System.out.println();
            */
 
            // Send the message and get the reply
            SOAPMessage reply = connection.call(message, destination);
 
            // Retrieve the result - no error checking is done: BAD!
            soapPart = reply.getSOAPPart();
            envelope = soapPart.getEnvelope();
            body = envelope.getBody();
 
            Iterator iter = body.getChildElements();
            Node resultOuter = ((Node) iter.next()).getFirstChild();
            Node result = resultOuter.getFirstChild();
 
            System.out.println("add("+arg1+","+arg2+") = "+result.getNodeValue());
 
            // Check the output
            /*
            System.out.println("\nResponse:\n");
            // Create the transformer
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            // Extract the content of the reply
            Source sourceContent = reply.getSOAPPart().getContent();
            // Set the output for the transformation
            StreamResult result = new StreamResult(System.out);
            transformer.transform(sourceContent, result);
            System.out.println();
            */
 
            // Close the connection           
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
 
    // This method demonstrates setting HTTP headers (for authentication)
    // and SOAP headers. Both of these are optional.
 
    // Note: There's a bug in some Axis2 versions (https://issues.apache.org/jira/browse/AXIS2-1014)
    // that prevents SOAP/MIME headers from being transmitted for SAAJ clients. 
    // It has been fixed in Axis2 versions 1.5.3 and 1.6.0.
 
//    private void setOptionalHeaders (SOAPMessage message, SOAPEnvelope envelope) throws SOAPException
//    {
//        // start: setting HTTP headers - optional, comment out if not needed
//            String username = "wsuser";
//            String password = "wspwd";
// 
//            String authorization = new sun.misc.BASE64Encoder().encode((username+":"+password).getBytes());
//            MimeHeaders hd = message.getMimeHeaders();
//            hd.addHeader("Authorization", "Basic " + authorization);
//        // end: setting HTTP headers
// 
//        // start: setting SOAP headers - optional, comment out if not needed
//            // Create and populate the header
//            SOAPHeader header = message.getSOAPHeader();
//            if (header == null)
//                header = envelope.addHeader();
// 
//            // Will use the default actor "next" for this example,
//            // otherwise use he.setActor(String actorURI) to define actor.
//            SOAPHeaderElement he = header.addHeaderElement(
//                    envelope.createName("foo", "ns1", "http://localhost/SOAPHeader"));
//            he.setMustUnderstand(false);
//            he.setValue("bar");
//        // end: setting SOAP headers
//    }
}
