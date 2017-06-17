package jku.students.at;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Florian on 17/06/2017.
 */
public class OWLHandler {

    OWLOntologyManager owlManager;
    OWLDataFactory owlFactory;

    final IRI ONTOLOGY_IRI_PREFIX = IRI.create("http://www.semanticweb.org/david/ontologies/2017/5/untitled-ontology-5#");
    final String ONTOLOGY_FILE = "resources/university.owl";

    public OWLHandler(){
        this.owlManager = OWLManager.createOWLOntologyManager();
        this.owlFactory = OWLManager.getOWLDataFactory();
    }

    public OWLClass getOntologyClass(String name){
        return owlFactory.getOWLClass(IRI.create(ONTOLOGY_IRI_PREFIX + name));
    }

    public OWLDataProperty getOntologyDataProperty(String name){
        return  owlFactory.getOWLDataProperty(IRI.create(ONTOLOGY_IRI_PREFIX + name));
    }

    public void execute() throws OWLOntologyCreationException {
        OWLOntology ontologyObj = owlManager.loadOntologyFromOntologyDocument(new File(ONTOLOGY_FILE));

        OWLReasoner reasoner=new Reasoner.ReasonerFactory().createReasoner(ontologyObj);

        OWLAxiom owlAxiom = owlFactory.getOWLEquivalentClassesAxiom(this.getOntologyClass("Lecturer"), this.getOntologyClass("Professor"));
        AddAxiom addAxiom = new AddAxiom(ontologyObj, owlAxiom);
        owlManager.applyChange(addAxiom);

        owlAxiom = owlFactory.getOWLSubClassOfAxiom(this.getOntologyClass("FullProfessor"), this.getOntologyClass("VisitingProfessor"));
        addAxiom = new AddAxiom(ontologyObj, owlAxiom);
        owlManager.applyChange(addAxiom);

        reasoner=new Reasoner.ReasonerFactory().createReasoner(ontologyObj);

        for (OWLClass c : reasoner.getEquivalentClasses(this.getOntologyClass("Employee")).getEntities()){
            System.out.println(" " + c.getIRI().getFragment());
        }

        printHierarchy(reasoner, this.owlFactory.getOWLThing(), 0, new HashSet<OWLClass>());

    }


    private void printHierarchy(OWLReasoner reasoner, OWLClass clazz, int level, Set<OWLClass> visited){
        if (!visited.contains(clazz) && reasoner.isSatisfiable(clazz)){
            visited.add(clazz);
            for (int i = 0; i < level * 4; i++){
                System.out.print(" ");
            }
            System.out.println(labelFor(clazz, reasoner.getRootOntology()));

            NodeSet<OWLClass> classes = reasoner.getSubClasses(clazz, true);
            for (OWLClass child : classes.getFlattened()){
                printHierarchy(reasoner, child, level + 1, visited);
            }
        }
    }

    private LabelExtractor le = new LabelExtractor();
    private String labelFor(OWLEntity clazz, OWLOntology o) {
        Set<OWLAnnotation> annotations = clazz.getAnnotations(o);
        for (OWLAnnotation anno : annotations) {
            String result = anno.accept(le);
            if (result != null) {
                return result;
            }
        }
        return clazz.getIRI().toString();
    }
}

class LabelExtractor extends OWLObjectVisitorExAdapter<String>
        implements OWLAnnotationObjectVisitorEx<String> {
    @Override
    public String visit(OWLAnnotation annotation) {
        if (annotation.getProperty().isLabel()) {
            OWLLiteral c = (OWLLiteral) annotation.getValue();
            return c.getLiteral();
        }
        return null;
    }
}
