package tk.trentoleaf.cineweb;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.util.PSQLException;
import tk.trentoleaf.cineweb.db.DB;
import tk.trentoleaf.cineweb.exceptions.*;
import tk.trentoleaf.cineweb.model.*;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tk.trentoleaf.cineweb.email.EmailUtils;
import static org.junit.Assert.*;

public class EmailTest {

    @Test
    public void test() {
        String s = EmailUtils.registrationText("nome", "cognome", "url");
        String c = EmailUtils.registrationText("Williams", "Rizzi", "www.williams.it");
        String t = EmailUtils.registrationText("Trento", "Leaf+", "www.trentoleaf.tk");

        int a=2;
    }


}