package tn.esprit.test;

public class Main {


    public static void main(String[] args) {
//        Test t1 = Test.getInstance();
//        Test t2 = Test.getInstance();
//
//
//        System.out.println(t1);
//        System.out.println(t2);

        ServicePersonne sp = new ServicePersonne();

        sp.add(new Personne(10,"bouhaja","omar"));


        System.out.println(sp.getAll());
    }

}
