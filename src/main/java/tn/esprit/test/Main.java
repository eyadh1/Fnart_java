package tn.esprit.test;

import tn.esprit.models.beneficiaires;
import tn.esprit.services.ServicesBeneficiaires;
import tn.esprit.utils.MyDataBase;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        Connection connection=MyDataBase.getInstance().getCnx();
        Connection connection1=MyDataBase.getInstance().getCnx();

        System.out.println(connection);
        System.out.println(connection1);

        ServicesBeneficiaires beneficiairesService = new ServicesBeneficiaires() {

            };
        };
        // ServicesBeneficiaires.add(new beneficiaires("jnen","marwen@gmail.com",22250300,"club","oui","description"));
        //   personneService.update(new Personne(1,"ben","ali",24));
        //System.out.println(ServicesBeneficiaires.select());
    }

