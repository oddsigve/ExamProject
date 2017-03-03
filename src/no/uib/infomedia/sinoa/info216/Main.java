package no.uib.infomedia.sinoa.info216;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Iterator;

import java.nio.file.Files;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

public class Main {

	/**
     * Query et Endpoint med bruk av Sparql Query
     * @param Query1
     * @param EndpointDb
     * @throws Exception
     */
    public void queryEndpoint(String query1, String endpointDb) throws Exception {
        //  Lager en Query med Stringen query
        Query query = QueryFactory.create(query1);

        // Lager Query Execution Factory 
        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpointDb, query);

        // Setter Timeout 
        ((QueryEngineHTTP)qexec).addParam("timeout", "10000");


        // Utfører query
        int counter = 0;
        ResultSet resultSet = qexec.execSelect();
        while (resultSet.hasNext()) {
            // Få Resultat
            QuerySolution querySolution = resultSet.next();

            //Få variable navn
            Iterator<String> vars = querySolution.varNames();

            // Teller
            counter++;
            System.out.println("Result " + counter + ": ");

            // Vis resulat
            while (vars.hasNext()) {
                String var = vars.next().toString();
                String val = querySolution.get(var).toString();
                
                System.out.println("[" + var + "]: " + val);
            }
        }
    } // Slutt på metode: queryEndpoint()


    public static void main(String[] args) throws IOException {
        // SPARQL Query 1 Kjente personer født samme dato som meg og informasjon om hvem de er
        String query1 = " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
        		+ " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
        		+ " PREFIX : <http://dbpedia.org/resource/> "
        		+ " PREFIX dbo:<http://dbpedia.org/ontology/>"
        		+ " SELECT ?person ?birthDate ?abstract "
        		+ " WHERE "
        		+ " { "
        		+ " ?person a dbo:Person . "
        		+ " ?person dbo:birthDate ?birthDate . Filter(str(?birthDate) = '1984-09-15') . "
        		+ " ?person dbo:abstract ?abstract . Filter(lang(?abstract) = 'en') . "	
				+ " } "
        		+ "LIMIT 100";
        String query2 = " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
        		+ " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
        		+ " PREFIX : <http://dbpedia.org/resource/> "
        		+ " PREFIX dbo:<http://dbpedia.org/ontology/>"
        		+ " SELECT ?person ?birthDate ?abstract "
        		+ " WHERE "
        		+ " { "
        		+ " ?person a dbo:Person . "
        		+ " ?person dbo:birthDate ?birthDate . Filter(str(?birthDate) = '1984-09-15') . "
				+ " } ";
        // Argumenter
        if (args != null && args.length == 1) {
            query1 = new String(
                    Files.readAllBytes(Paths.get(args[0])),
                    Charset.defaultCharset());
            query2 = new String(
                    Files.readAllBytes(Paths.get(args[0])),
                    Charset.defaultCharset());
        }

        // DBPedia Sparql Endpoint
        String endpointDb = "http://dbpedia.org/sparql";
        String endpointLMBD = "http://data.linkedmdb.org/sparql";

        // Query DBPedia
        try {
            Main q = new Main();
            q.queryEndpoint(query1, endpointDb);
            q.queryEndpoint(query2, endpointLMBD);
        }
        catch (Exception ex) {
            System.err.println(ex);
        }
    }
}

