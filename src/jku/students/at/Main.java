package jku.students.at;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class Main {

    public static void main(String[] args) {
	// write your code here


        try {
            OWLHandler handler = OWLHandler.getInstance();
            handler.execute();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
    }
}
