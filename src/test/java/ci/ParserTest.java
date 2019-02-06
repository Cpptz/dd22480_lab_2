package ci;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void parseJson() {

		// json string for testing the parser
		String jsondata = "{ \"ref\": \"refs/heads/master\", \"before\": \"3f82f6298cc95f973c55a4db97eac0c8aabc675d\", \"after\": \"ec35c54ffbda84a1392618d84bedd3a39d82401f\", \"created\": false, \"deleted\": false, \"forced\": false, \"base_ref\": null, \"compare\": \"https://github.com/sork01/webhooks/compare/3f82f6298cc9...ec35c54ffbda\", \"commits\": [ { \"id\": \"ec35c54ffbda84a1392618d84bedd3a39d82401f\", \"tree_id\": \"b99edbc4d2574a62faa8ea550a22d30bb89e6b8d\", \"distinct\": true, \"message\": \"test\", \"timestamp\": \"2019-02-05T18:06:09+01:00\", \"url\": \"https://github.com/sork01/webhooks/commit/ec35c54ffbda84a1392618d84bedd3a39d82401f\", \"author\": { \"name\": \"sork01\", \"email\": \"ro.gunning@gmail.com\", \"username\": \"sork01\" }, \"committer\": { \"name\": \"GitHub\", \"email\": \"noreply@github.com\", \"username\": \"web-flow\" }, \"added\": [], \"removed\": [], \"modified\": [ \"README.md\" ] } ] }";
        
        Parser p = new Parser();
		Commit c = p.parseCommit(jsondata);
		String sha = "ec35c54ffbda84a1392618d84bedd3a39d82401f";
		String url = "https://github.com/sork01/webhooks/commit/ec35c54ffbda84a1392618d84bedd3a39d82401f";
        String ref = "refs/heads/master";
		String timestamp = "2019-02-05T18:06:09+01:00";
		String name = "sork01";
		String message = "test";
		
		// these should all be equal
        assertEquals(c.sha, sha);
        assertEquals(c.url, url);
        assertEquals(c.ref, ref);
        assertEquals(c.timestamp, timestamp);
        assertEquals(c.name, name);
        assertEquals(c.message, message);
    }

}