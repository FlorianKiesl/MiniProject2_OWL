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
    OWLOntology ontologyObj;

    final IRI ONTOLOGY_IRI_PREFIX = IRI.create("http://www.semanticweb.org/david/ontologies/2017/5/untitled-ontology-5#");
    final String ONTOLOGY_FILE = "resources/university.owl";

    public OWLHandler(){

    }

    public void load() throws OWLOntologyCreationException {
        this.owlManager = OWLManager.createOWLOntologyManager();
        this.owlFactory = OWLManager.getOWLDataFactory();
        this.ontologyObj = owlManager.loadOntologyFromOntologyDocument(new File(ONTOLOGY_FILE));
    }

    private OWLClass getOntologyClass(String name){
        return owlFactory.getOWLClass(IRI.create(ONTOLOGY_IRI_PREFIX + name));
    }

    private OWLObjectProperty getOntologyObjectProperties(String name){
        return  owlFactory.getOWLObjectProperty(IRI.create(ONTOLOGY_IRI_PREFIX + name));
    }

    private OWLDataProperty getOntologyDataProperty(String name){
        return  owlFactory.getOWLDataProperty(IRI.create(ONTOLOGY_IRI_PREFIX + name));
    }

    public void addClassToThingAxiom(String clazz){
        OWLAxiom owlAxiom = owlFactory.getOWLSubClassOfAxiom(this.getOntologyClass(clazz), owlFactory.getOWLThing());
        this.addClassAxiom(owlAxiom);
    }

    public void deleteClassToThingAxiom(String clazz){
        OWLAxiom owlAxiom = owlFactory.getOWLSubClassOfAxiom(this.getOntologyClass(clazz), owlFactory.getOWLThing());
        this.deleteClassAxiom(owlAxiom);
    }

    public void addSubClassAxiom(String subClass, String superClass){
        OWLAxiom owlAxiom = owlFactory.getOWLSubClassOfAxiom(this.getOntologyClass(subClass), this.getOntologyClass(superClass));
        this.addClassAxiom(owlAxiom);
    }

    public void deleteSubClassAxiom(String subClass, String superClass){
        OWLAxiom owlAxiom = owlFactory.getOWLSubClassOfAxiom(this.getOntologyClass(subClass), this.getOntologyClass(superClass));
        this.deleteClassAxiom(owlAxiom);
    }

    public void addEquivalentClassAxiom(String class1, String class2){
        this.addEquivalentClassAxiom(class1, this.getOntologyClass(class2));
    }

    public void addEquivalentClassAxiom(String clazz, OWLClassExpression expr){
        OWLAxiom owlAxiom = owlFactory.getOWLEquivalentClassesAxiom(this.getOntologyClass(clazz), expr);
        this.addClassAxiom(owlAxiom);
    }

    public void deleteEquivalentClassAxiom(String class1, String class2){
        OWLAxiom owlAxiom = owlFactory.getOWLEquivalentClassesAxiom(this.getOntologyClass(class1), this.getOntologyClass(class2));
        this.deleteClassAxiom(owlAxiom);
    }

    private void addClassAxiom(OWLAxiom owlAxiom){
        AddAxiom addAxiom = new AddAxiom(ontologyObj, owlAxiom);
        owlManager.applyChange(addAxiom);
    }

    private void deleteClassAxiom(OWLAxiom owlAxiom){
        RemoveAxiom removeAxiom = new RemoveAxiom(ontologyObj, owlAxiom);
        owlManager.applyChange(removeAxiom);
    }


    public void execute() throws OWLOntologyCreationException {

        OWLReasoner reasoner= new Reasoner.ReasonerFactory().createReasoner(ontologyObj);

        System.out.println(this.getOntologyClass("Hallo"));

        this.addClassToThingAxiom("Hallo");

        this.deleteSubClassAxiom("Faculty", "University");
        this.deleteSubClassAxiom("Department", "Faculty");
        this.deleteClassToThingAxiom("Hallo");

        OWLAxiom owlAxiom = owlFactory.getOWLEquivalentClassesAxiom(this.getOntologyClass("Lecturer"), this.getOntologyClass("Professor"));
        AddAxiom addAxiom = new AddAxiom(ontologyObj, owlAxiom);
        owlManager.applyChange(addAxiom);

        this.addSubClassAxiom("FullProfessor", "VisitingProfessor");
        this.deleteSubClassAxiom("FullProfessor", "VisitingProfessor");

        reasoner=new Reasoner.ReasonerFactory().createReasoner(ontologyObj);
        System.out.println("Ontology is consistent: " + reasoner.isConsistent());

        for (OWLClass c : reasoner.getEquivalentClasses(this.getOntologyClass("Lecturer")).getEntities()){
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
