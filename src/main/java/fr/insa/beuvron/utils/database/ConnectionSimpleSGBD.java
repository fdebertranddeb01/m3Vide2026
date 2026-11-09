/*
Copyright 2000- Francois de Bertrand de Beuvron

This file is part of CoursBeuvron.

CoursBeuvron is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CoursBeuvron is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.beuvron.utils.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Quelques méthodes utilitaires pour se connecter à divers SGBD.
 * <p>
 * fourni également une petite méthode pour tenter de définir les clés primaire
 * indépendemment du SGBD
 * </p>
 * <p>
 * pour l'instant uniquement les SGBD utilisés dans le module M3 pour l'année
 * 2024, mais il serait facile d'en inclure d'autres sur le même principe
 * </p>
 *
 * @author francois
 */
public class ConnectionSimpleSGBD {

    /**
     * permet de selectionner le SGBD utilisé
     */
    public static SGBDPossibles SGBD = SGBDPossibles.H2FILE;

    // à modifier
    public static String LOGIN_SUR_SERVEUR_M3 = "m3_fdebertranddeb01";
    public static String PASS_SUR_SERVEUR_M3 = "donnez le votre";

    /**
     * ATTENTION : chaque appel à defautCon crée une nouvelle connection à la
     * BdD. Vous ne devez pas l'utiliser à chaque fois que vous voulez accéder à
     * la base de donnée. Vous devez conserver la connection renvoyée par
     * defaultCon pour la ré-utiliser.
     *
     * @return
     */
    public static Connection defaultCon() throws SQLException {
        if (SGBD == SGBDPossibles.MYSQL) {
            return mysqlServeurPourM3();
        } else if (SGBD == SGBDPossibles.H2FILE) {
            return h2InFile("./h2BdD/bdd");
        } else if (SGBD == SGBDPossibles.H2MEMORY) {
            return h2InMemory("h2BdD");
        } else {
            throw new Error("SGBD non couvert par ConnectionSimpleSGBD");
        }
    }

    /**
     * demande une connection à un serveur MySQL
     */
    public static Connection connectMySQL(String host, int port,
            String database, String user, String pass) throws SQLException {
        // ce test de la classe du driver n'est plus censée être indispensable
        // mais j'ai des problème si je ne l'utilise pas lorsque je porte
        // mon application web dans un serveur tomcat
        // donc je l'ajoute.
        Class dmysql = com.mysql.cj.jdbc.Driver.class;
        Logger.getGlobal().log(Level.INFO, "Driver mysql {0} found", dmysql.getCanonicalName());
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://" + host + ":" + port + "/" + database, database, pass);
        // fixe le plus haut degré d'isolation entre transactions
        // risque de dégrader les performances si l'on avait de nombreuses connections
        // simultanée.
        // Dans le cadre d'un module d'enseignement, on veut que le SGBD soit
        // dans le mode le plus sur possible
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        return con;
    }

    /**
     * Un serveur mysql installé pour le module M3.
     * <p>
     * le nom de la base de donnée est le même que le nom d'utilisateur d'où le
     * doublon dans l'appel.
     * </p>
     *
     * @return
     * @throws SQLException
     */
    public static Connection mysqlServeurPourM3() throws SQLException {
        return connectMySQL("92.222.25.165", 3306,
                LOGIN_SUR_SERVEUR_M3,
                LOGIN_SUR_SERVEUR_M3,
                PASS_SUR_SERVEUR_M3);
    }

    /**
     * la base de donnée est crée en mémoire.
     * <p>
     * H2 est un SGBD qui permet de créer des bases de données en mémoire
     * </p>
     * <p>
     * La BdD est donc perdue (et doit être recrée) à chaque démarrage du
     * programme. Il est clair que cela n'est utile qu'en phase de développement
     * </p>
     * <p>
     * Clairement la BdD n'est pas accessible sur d'autres ordinateur. On n'est
     * pas du tout en architecture client-serveur.
     * </p>
     *
     * @param name vous pouvez donner un nom quelconque, par exemple "test".
     * Cela ne sert que dans le cas exceptionnel ou vous voulez avoir deux bases
     * de données distinctes en mémoire : il suffit alors de leur donner deux
     * noms différents.
     * @return
     */
    public static Connection h2InMemory(String name) throws SQLException {
        // comme pour mysql, j'essaye de forcer la prise en compte du driver
        Class dh2 = org.h2.Driver.class;
        Logger.getGlobal().log(Level.INFO, "Driver h2 {0} found", dh2.getCanonicalName());
        return DriverManager.getConnection("jdbc:h2:mem:" + name, "H2User", "H2Pass");
    }

    /**
     * la base de donnée est crée en dans des fichiers sur l'odinateur.
     * <p>
     * H2 permet également de conserver la BdD dans des fichiers de l'ordinateur
     * courant.
     * </p>
     * <p>
     * La BdD est conservé entre deux exécutions du programme
     * </p>
     * <p>
     * Mais la BdD n'est pas accessible sur d'autres ordinateur. On n'est pas du
     * tout en architecture client-serveur.
     * </p>
     *
     * @param path le répertoire où sera sauvegarder les fichiers de la BdD. Le
     * plus simple est de donner un chemin relatif, par exemple "bdd". Cela
     * créera un dossier "bdd" dans le répertoire principal du projet.
     * @return
     */
    public static Connection h2InFile(String path) throws SQLException {
        // comme pour mysql, j'essaye de forcer la prise en compte du driver
        Class dh2 = org.h2.Driver.class;
        Logger.getGlobal().log(Level.INFO, "Driver h2 {0} found", dh2.getCanonicalName());
        return DriverManager.getConnection("jdbc:h2:file:" + path, "H2User", "H2Pass");
    }

}
