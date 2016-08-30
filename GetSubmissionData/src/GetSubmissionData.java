import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.w3c.dom.Document;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class GetSubmissionData {
    public static void main(String[] args) throws Exception {
    	
    	/*								 Example of Arguments passed in:								*/
    	// orderFilePath = "C:\\Users\\developer\\workspace\\MetroBureau\\xml\\input_orders\\";
    	// responseFilePath = "C:\\Users\\developer\\workspace\\MetroBureau\\xml\\output_responses\\";
    	// requestID = 99999999999  generated int
    	// order = "XML_TestCTOrder";
    	// user = "TEST1";
    	// password = "PASS123";
    	// orderType = "ord_";
    	// bureauType = "Metro";
    	
    	String orderFilePath = args[0];    	
    	String responseFilePath = args[1];
    	String requestID = args[2];
    	String order = args[3];
    	String user = args[4];
    	String password = args[5];
    	// // this allows for many different types of order XML files (ord_ = submit order, inq_ = check for responses, inqconf_ confirmation of inquiry received ... etc.)
    	String orderType = args[6]; 		
    	String bureauType = args[7];
    
    	String msg = "";
    	String xmlOrderOut = responseFilePath + orderType + "resp_" + requestID + ".xml";
    	String responseBody = "";
    	
    	String bureauCheck = bureauType;  //"Metro";
    
    	// POST the order - get the response
    	if( bureauType.equals("Metro") ){
    		
    		msg = "In method main of GetSubmissionData\nRunning postItMetro( " + bureauType +" )\n\n";
    		System.out.println(msg);
    		responseBody = postItMetro( args );
    		msg = "After running postItMetro( " + bureauType +" ), responseBody is: \n" + responseBody + "\n\n";
    		System.out.println(msg);
    		
    	}else if(bureauType.equals("ISO")){
    		msg = "In method main of GetSubmissionData\nRunning postItISO ( " + bureauType +" )\n\n";
    		System.out.println(msg);
    		responseBody = postItISO( args );
    		msg = "After running postItISO(" + bureauType + "), responseBody is: \n" + responseBody + "\n\n";
    		System.out.println(msg);
    		
    	}
    	else {
    		
    		msg = "Invalid bureauType ( " + bureauType + " ) in method main of GetSubmissionData\n\n";
    		System.err.println(msg);
    		return;
    		
    	}
    	
     	//send params and raw XML back to the VRM in STDOUT
    	System.out.println("STD OUT responseBody:\n----------------------------------------------\n" + responseBody);
         
    	try {
    		
    		System.out.println("STD OUT save raw XML to file using the name in xmlOrderOut variable....\n");
    		
        	//save raw XML to file using the name in xmlOrderOut variable
            File newXMLFile = new File(xmlOrderOut);

            FileWriter fw = new FileWriter(newXMLFile);
            fw.write(responseBody);
            fw.close();

        } catch (IOException iox) {
        	System.out.println("STD OUT save raw XML to file (" + xmlOrderOut + ") FAILED!!!! In Catch !!!! ....\n");
    		
            //do stuff with exception
            iox.printStackTrace();
        }
    } 
 
	public static Document loadXMLFromString( String xml) throws Exception
	{
		Document theDocument = null;
		try{
			System.err.println("String XML in loadXMLFromString:\n----------------------------------------------\n" + xml);
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    InputSource is = new InputSource(new StringReader(xml));
		    theDocument = builder.parse(is);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return theDocument;
	}
	
	public static String checkResponseMetro( Map<String, String> aMap, String requestID) throws Exception
	{

		String msg = "";
			
		// aMap = key/values of values from the response!
		// stdout
		System.err.println("Account = " + aMap.get("Account")+ "\n");
		System.err.println("BillingAccount = " + aMap.get("BillingAccount")+ "\n");
		System.err.println("RequestID = " + aMap.get("RequestID")+ "\n");
		System.err.println("RequestedCount = " + aMap.get("RequestedCount")+ "\n");
			
		// determine if we have an error:
		if(aMap.get("RejectedCount") != "0" ){
			msg = "Error submitting Metro Bureau Order for Request ID "+aMap.get("RequestID")+"("+requestID+").\nRequest was rejected on " + aMap.get("ProcessDate")+" at "+aMap.get("ProcessTime") +"\nStatus is "+aMap.get("Status")  +"\nResult is "+aMap.get("Result")  +"\nTransID is "+aMap.get("TransID") +"\nProcessID is "+aMap.get("ProcessID") +"\n";
			System.err.println(msg);
		}
			
		return msg;		
	}
	
	
	public static void parseNodes( Map<String, String> aMap, NodeList nodeList) throws Exception
	{
		for(int i = 0, len = nodeList.getLength(); i < len; i++){
			Node currentNode = nodeList.item(i);
			if(currentNode.getNodeType() == Node.ELEMENT_NODE){
				
			// variables
			String nodeName = "";
			String nodeText = "";
			int nodeChildCount = 0;
				
			//String nodeName = currentNode
			nodeName = currentNode.getNodeName().substring(4);
			
			// how many child nodes?
			nodeChildCount = currentNode.getChildNodes().getLength();
				
			// handle multiple or single child node(s)
			if(nodeChildCount > 1){
				System.err.println("===================================================\n");
				System.err.println("Start: " + nodeName+ " Child Nodes\n");

				// parse multiple text nodes
				// get first child node
				 NodeList nList = currentNode.getChildNodes();
					 
				 // recursively parse nodes
				 parseNodes( aMap, nList);
				 System.err.println("End: " + nodeName+ " Child Nodes\n");
				 System.err.println("===================================================\n");
			} else {
				// parse single text node
				// parse it
				nodeText = currentNode.getTextContent();
					
				// map it
				aMap.put(nodeName , nodeText);
					
				// list it
				System.err.println("Node: " + nodeName+ " = "+nodeText+ "\n");				 
				}
			}
		}
	}
	
	public static String postItMetro(String[] args) throws Exception {
		String orderFilePath = args[0];    	
		String responseFilePath = args[1];
		String requestID = args[2];
		String order = args[3];
		String user = args[4];
		String password = args[5];
		// this allows for many different types of order XML files (ord_ = submit order, inq_ = check for responses, inqconf_ confirmation of inquiry received ... etc.)
		String orderType = args[6]; 		
		String bureauType = args[7];
	
		HttpPost httppost = null;
	
		String xmlOrderIn = orderFilePath + orderType + requestID + ".xml";
	
		HttpClient httpclient = HttpClientBuilder.create().build();
		
		// set URL dependent upon bureauType
		httppost = new HttpPost("https://metroweb.metroreporting.com/cgi-bin/onegate");
		
		MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
	
		// set mode
		reqEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	
		// set boundary marker
		reqEntity.setBoundary(requestID);
    
		// build file body
		final File MYXML = new File(xmlOrderIn);
		FileBody fb = new FileBody(MYXML);
	
		reqEntity.addTextBody("onegate_set", order);
		reqEntity.addTextBody("user", user);
		reqEntity.addTextBody("passwd", password);
		reqEntity.addPart("xml_file", fb);
		
		// final assembly of file body
		final HttpEntity MYENTITY = reqEntity.build();
	
		// give the entity to the httppost
		httppost.setEntity(MYENTITY);
    
		//input file path:
    
		System.err.println("STD ERR xmlOrderIn:\n---------------------------\n" + xmlOrderIn +"\n\n");
    
		System.err.println("STD ERR Requesting:\n---------------------------\n" + httppost.getRequestLine() +"\n\n");
    
		// prepare output stream
		ByteArrayOutputStream out = new ByteArrayOutputStream((int)MYENTITY.getContentLength());
		MYENTITY.writeTo(out);
    
		// for debugging, convert to string and print
		String entityContentAsString = new String(out.toByteArray());
		System.err.println("STD ERR XML Part:\n-------------------------------------------------------\n" + entityContentAsString);
    
		// submit the order - get the response
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseBody = httpclient.execute(httppost, responseHandler);
    
		return responseBody;
	}

	public static String postItISO( String[] args ) throws Exception {
		String orderFilePath = args[0];    	
		String responseFilePath = args[1];
		String requestDate = args[2];
		String order = args[3];
		String user = args[4];
		String password = args[5];
		String orderType = args[6]; 		
		String bureauType = args[7];

		HttpPost httppost = null;

		String xmlOrderIn = orderFilePath + orderType + requestDate + ".xml";

		HttpClient httpclient = HttpClientBuilder.create().build();
	
		// set URL dependent upon bureauType
		httppost = new HttpPost("https://claimsearchgwa.iso.com/xmlwebservice");
	
		MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();

		// set mode
		reqEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

		// set boundary marker
		reqEntity.setBoundary(requestDate);

		// build file body
		final File MYXML = new File(xmlOrderIn);
		FileBody fb = new FileBody(MYXML);

		reqEntity.addTextBody("onegate_set", order);
		reqEntity.addTextBody("user", user);
		reqEntity.addTextBody("passwd", password);
		reqEntity.addPart("xml_file", fb);
	
		// final assembly of file body
		final HttpEntity MYENTITY = reqEntity.build();

		// give the entity to the httppost
		httppost.setEntity(MYENTITY);

		//input file path:

		System.err.println("STD ERR xmlOrderIn:\n---------------------------\n" + xmlOrderIn +"\n\n");
		System.err.println("STD ERR Requesting:\n---------------------------\n" + httppost.getRequestLine() +"\n\n");
	
		//prepare output stream
		ByteArrayOutputStream out = new ByteArrayOutputStream((int)MYENTITY.getContentLength());
		MYENTITY.writeTo(out);

		// for debugging, convert to string and print
		String entityContentAsString = new String(out.toByteArray());
		System.err.println("STD ERR XML Part:\n-------------------------------------------------------\n" + entityContentAsString);

		// submit the order - get the response
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseBody = httpclient.execute(httppost, responseHandler);
		return responseBody;
	}

	public static void printMap(Map<?, ?> mp) {
		Iterator<?> it = mp.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			System.err.println(pair.getKey() + " = " + pair.getValue());
			it.remove(); // avoids a ConcurrentModificationException
		}
	}

	public static String jsonMap(Map<?, ?> mp) {
		Iterator<?> it = mp.entrySet().iterator();
		String jsonOut = "{";
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			jsonOut = jsonOut + "\"" + pair.getKey() + "\":\"" + pair.getValue() + "\",";
		}
		
		// remove last comma and add ending }
		int jsonLen = jsonOut.length() - 2;
		jsonOut = jsonOut.substring( 0, jsonLen ) + "}";
    
		return jsonOut;
	}
} // end main 

