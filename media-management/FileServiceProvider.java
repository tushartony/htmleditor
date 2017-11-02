package com.ofs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
//import javax.ws.rs.core.mycollectionType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/service")
public class FileServiceProvider {
	static Logger logger = Logger.getLogger(FileServiceProvider.class);
	
	Response response = null;
	String status = null;
//	public static int count = 0;
	
	
	@POST
	@Path("/mypost")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("text/html")
	public String downloadCSV1(			
			String result
			) throws Exception
	{
		
		System.out.println("POST ****"+result);	

		return "Success";	

	}	

	
	 
	    @GET
		@Path("/getAllDocuments/colName/{colName}")
		@Produces("text/html")
		public String getAllDocumentsByCollectionName(@PathParam("colName")  String colName) throws IOException {

			StringBuffer jsonRes = new StringBuffer();
			
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB mongoDB = mongoClient.getDB("testdb");

			//Let's store the standard data in regular collection
			DBCollection collection = mongoDB.getCollection(colName);

			 DBCursor cursor = collection.find();
			 
			 jsonRes.append("{\"data\":[");
			 
			    while(cursor.hasNext()) {
			    	
			    	jsonRes.append(cursor.next().toString());
			    	
			    	if(cursor.hasNext())
			    	jsonRes.append(',');
			    	
			       
			    }
			    
			    jsonRes.append("]}");
			    
			    System.out.println(jsonRes);
			    return jsonRes.toString();			
	    }
	    
	
	

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("text/html")
	public Response uploadFile(		
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileInputDetails,
			@FormDataParam("fileId") String  fileId) throws UnknownHostException {
		
		System.out.println("FileId is :"+fileId+" ***** File name is :"+fileInputDetails.getFileName());
		
		
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		DB mongoDB = mongoClient.getDB("testdb");

		//Let's store the standard data in regular collection
		DBCollection collection = mongoDB.getCollection("media");

		BasicDBObject query = new BasicDBObject();
		query.put("_id", fileId);
		DBCursor cursor = collection.find(query);

		if (!cursor.hasNext()) {
			// Build our document and add all the fields
			BasicDBObject document = new BasicDBObject();
			document.append("_id", fileId);
			document.append("filename", fileInputDetails.getFileName());


			//insert the document into the collection 
			collection.insert(document);

			// Now let's store the binary file data using mycollection GridFS  
			GridFS fileStore = new GridFS(mongoDB, "media");
			GridFSInputFile inputFile = fileStore.createFile(fileInputStream);
			inputFile.setId(fileId);
			inputFile.setFilename(fileInputDetails.getFileName());
			inputFile.save();

			 status = "Sucessfully Uploaded!";

			logger.info("ID: " + fileId);

			logger.info("fileInputDetails: " + fileInputDetails);

			
			
		} else {
			 status = "Unable to insert record with ID: " + fileId +" as record already exists!!!";
			//logger.info(status);
						
		}
		
		return Response.status(200).entity(status).build();
	}

	@GET
	@Path("/download/file/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadFilebyID(@PathParam("id")  String id) throws IOException {

		
		
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		DB mongoDB = mongoClient.getDB("testdb");

		//Let's store the standard data in regular collection
		DBCollection collection = mongoDB.getCollection("media");

		logger.info("Inside downloadFilebyID...");
		logger.info("ID: " + id);

		BasicDBObject query = new BasicDBObject();
		query.put("_id", id);
		DBObject doc = collection.findOne(query);
		DBCursor cursor = collection.find(query);

		if (cursor.hasNext()) {

			Set<String> allKeys = doc.keySet();
			HashMap<String, String> fields = new HashMap<>();
			for (String key: allKeys) {
				fields.put(key, doc.get(key).toString());
			}


			logger.info("filename: " + fields.get("filename"+".mp4"));
			
			GridFS fileStore = new GridFS(mongoDB, "media");
			
			
			
			GridFSDBFile gridFile = fileStore.findOne(query);

			InputStream in = gridFile.getInputStream();

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int data = in.read();
			while (data >= 0) {
				out.write((char) data);
				data = in.read();
			}
			out.flush();

			ResponseBuilder builder = Response.ok(out.toByteArray());
			builder.header("Content-Disposition", "attachment; filename=" + fields.get("filename"));
			response = builder.build();
		} else {
			response = Response.status(404).
					entity(" Unable to get file with ID: " + id).
					type("text/plain").
					build();
		}

		return response;
	}
	
	
	
	
	
@GET
	@Produces("video/mp4")
	@Path("/streamVideo/{id}")
	public Response streamVideo(@HeaderParam("Range") String range,
			@PathParam ("id") String id
			)  {
		
		try{
		MediaStreamer medaiStreamer = new MediaStreamer();
		
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		DB mongoDB = mongoClient.getDB("testdb");

		//Let's store the standard data in regular collection
		DBCollection collection = mongoDB.getCollection("media");

		logger.info("Inside downloadFilebyID...");
		logger.info("ID: " + id);

		BasicDBObject query = new BasicDBObject();
		query.put("_id", id);
		DBObject doc = collection.findOne(query);
		DBCursor cursor = collection.find(query);

		if (cursor.hasNext()) {

			Set<String> allKeys = doc.keySet();
			HashMap<String, String> fields = new HashMap<>();
			for (String key: allKeys) {
				fields.put(key, doc.get(key).toString());
			}


			logger.info("filename: " + fields.get("filename"));
			
			GridFS fileStore = new GridFS(mongoDB, "media");
			
			
			
			GridFSDBFile gridFile = fileStore.findOne(query);
			
			 File file = new File(" .wmv");
			gridFile.writeTo(file);	

			
				  
				//  File audio = new File("D:\\Wildlife.wmv");
			       
				  response = medaiStreamer.buildStream(file, range);
				  
		} 
		
		
		}catch(Exception e)
		{
			
		}
		return response;

		
	}
	
	
	
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/istreamVideo/{id}")
	public Response iStreamVideo(@HeaderParam("Range") String range,
			@PathParam ("id") String id
			)  {
		
		try{
		MediaStreamer mediaStreamer = new MediaStreamer();
		
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		DB mongoDB = mongoClient.getDB("testdb");

		//Let's store the standard data in regular collection
		DBCollection collection = mongoDB.getCollection("media");

		logger.info("Inside downloadFilebyID...");
		logger.info("ID: " + id);

		BasicDBObject query = new BasicDBObject();
		query.put("_id", id);
		DBObject doc = collection.findOne(query);
		DBCursor cursor = collection.find(query);

		if (cursor.hasNext()) {

			Set<String> allKeys = doc.keySet();
			HashMap<String, String> fields = new HashMap<>();
			for (String key: allKeys) {
				fields.put(key, doc.get(key).toString());
			}


			logger.info("filename: " + fields.get("filename"));
			
			GridFS fileStore = new GridFS(mongoDB, "media");
			
			
			
			GridFSDBFile gridFile = fileStore.findOne(query);
			
			 File file = new File("");
			gridFile.writeTo(file);	
			
			ResponseBuilder response = Response.ok(file, MediaType.APPLICATION_OCTET_STREAM);
			response.header("Content-Disposition",  "filename=videofile.wmv");
			return response.build();

			/*return Response.ok(file, mycollectionType.APPLICATION_OCTET_STREAM)
		            .build();*/
				  
				//  File audio = new File("D:\\Wildlife.wmv");
			       
				//  response = medaiStreamer.buildStream(file, range);
				  
		} 
		
		
		}catch(Exception e)
		{
			
		}
		return response;
	        

		
	}
	
	

	@GET
	@Path("/download/details/{id}")
	@Produces(MediaType.TEXT_HTML)
	public Response showFileStoreDetails(@PathParam("id")  String id) throws UnknownHostException {

		//Response response = null;
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		DB mongoDB = mongoClient.getDB("testdb");

		//Let's store the standard data in regular collection
		DBCollection collection = mongoDB.getCollection("media");

		logger.info("Inside showFileStoreDetails...");
		logger.info("ID: " + id);

		BasicDBObject query = new BasicDBObject();
		query.put("fileid", id);
		DBObject doc = collection.findOne(query);

		DBCursor cursor = collection.find(query);

		if (cursor.hasNext()) {
			Set<String> allKeys = doc.keySet();
			HashMap<String, String> fields = new HashMap<>();
			for (String key: allKeys) {
				fields.put(key, doc.get(key).toString());
			}


			logger.info("filename: " + fields.get("filename"));

			StringBuffer status1 = new StringBuffer("Inside showHeaders: <br/><br/>");
			status1.append("filename : ");
			status1.append(fields.get("filename"));
			status1.append("<br/>");

			response = Response.status(200).entity(status1.toString()).build();
		} else {
			response = Response.status(404).
					entity(" Unable to get file with ID: " + id).
					type("text/plain").
					build();
		}
		return response;
	

}
	}
