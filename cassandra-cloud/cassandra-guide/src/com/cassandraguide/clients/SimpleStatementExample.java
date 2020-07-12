/*
 * Copyright (C) 2016 Jeff Carpenter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cassandraguide.clients;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.QueryTrace;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;

import java.text.SimpleDateFormat;

public class SimpleStatementExample {
	
	public static void main(String[] args) {
		
		Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1")
				//.withCredentials("jeff", "i6XJsj!k#9")
				.build();
		
		// create session on the "hotel" keyspace
		Session session = cluster.connect("hotel");
		
		// create a Hotel ID
		String is = "AZ123";
		
		// create parameterized INSERT statement
		SimpleStatement hotelInsert = new SimpleStatement(
				"INSERT INTO hotels (id, name, phone) VALUES (?, ?, ?)",
				is, "Super Hotel at WestWorld", "1-888-999-9999");
		
		ResultSet hotelInsertResult = session.execute(hotelInsert);
		
		System.out.println(hotelInsertResult);
		System.out.println(hotelInsertResult.wasApplied());
		System.out.println(hotelInsertResult.getExecutionInfo());
		System.out.println(hotelInsertResult.getExecutionInfo().getIncomingPayload());
		
		// create parameterized SELECT statement
		SimpleStatement hotelSelect = new SimpleStatement(
				"SELECT * FROM hotels WHERE id=?", is);
		hotelSelect.enableTracing();
		
		
		ResultSet hotelSelectResult = session.execute(hotelSelect);
		
		// result metadata
		System.out.println(hotelSelectResult);
		System.out.println(hotelSelectResult.wasApplied());
		System.out.println(hotelSelectResult.getExecutionInfo());
		System.out.println(hotelSelectResult.getExecutionInfo().getIncomingPayload());
		System.out.println(hotelSelectResult.getExecutionInfo().getQueryTrace());
		
		// print results
		for (Row row : hotelSelectResult) {
			System.out.format("id: %s, name: %s, phone: %s\n\n", row.getString("id"), 
				row.getString("name"), row.getString("phone"));
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
		QueryTrace queryTrace = hotelSelectResult.getExecutionInfo().getQueryTrace();
		System.out.printf("Trace id: %s\n\n", queryTrace.getTraceId());
		System.out.printf("%-42s | %-12s | %-10s \n", "activity",
		   "timestamp", "source");
		System.out.println("-------------------------------------------+--------------+------------");
		      
		for (QueryTrace.Event event : queryTrace.getEvents()) {
		  System.out.printf("%42s | %12s | %10s\n",     
		     event.getDescription(),
		     dateFormat.format((event.getTimestamp())),
		     event.getSource());
		}

		
		// close and exit
		cluster.close();
		System.exit(0);
	}
		
		
}
