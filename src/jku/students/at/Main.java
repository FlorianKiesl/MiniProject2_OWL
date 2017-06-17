package jku.students.at;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class Main {

    public static void main(String[] args) {
	// write your code here
        OWLHandler handler = new OWLHandler();
        try {
            handler.execute();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
    }
}
