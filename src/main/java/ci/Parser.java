package ci;

import com.google.gson.*;

class Parser {
	    public static Commit parseCommit(String jsonLine) {
			Commit c = new Commit();
			JsonElement jelement = new JsonParser().parse(jsonLine);
			if (jelement == null)
			{ 
				System.out.println("Json element is null!"); 
			}
			c.ref = jelement.getAsJsonObject().get("ref").getAsString();
			JsonArray commits = jelement.getAsJsonObject().get("commits").getAsJsonArray();
			c.sha = commits.get(0).getAsJsonObject().get("id").getAsString();
			c.url = commits.get(0).getAsJsonObject().get("url").getAsString().substring(0,39);
			c.message = commits.get(0).getAsJsonObject().get("message").getAsString();
			c.timestamp = commits.get(0).getAsJsonObject().get("timestamp").getAsString();
			c.name = commits.get(0).getAsJsonObject().get("author").getAsJsonObject().get("name").getAsString();
			return c;
    }

}