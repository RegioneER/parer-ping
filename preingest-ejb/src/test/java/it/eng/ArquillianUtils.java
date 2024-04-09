/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public abstract class ArquillianUtils {

    public static JavaArchive createPingJar(Class clazz) {
        String warName = clazz == null ? "PingTests.jar" : clazz.getSimpleName() + "Tests.jar";
        JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class, warName);
        javaArchive
                .addAsResource(ArquillianUtils.class.getClassLoader().getResource("ejb-jar.xml"),
                        "META-INF/ejb-jar.xml")
                .addAsResource(ArquillianUtils.class.getClassLoader().getResource("jboss-ejb3.xml"),
                        "META-INF/jboss-ejb3.xml")
                .addAsResource(ArquillianUtils.class.getClassLoader().getResource("persistence.xml"),
                        "META-INF/persistence.xml")
                .addAsResource(EmptyAsset.INSTANCE, "META-INF/beans.xml");
        javaArchive.addPackage("it.eng.sequences.hibernate")
                .addClasses(it.eng.paginator.hibernate.OracleSqlInterceptor.class, ArquillianUtils.class,
                        org.apache.commons.lang3.StringUtils.class, it.eng.spagoCore.error.EMFError.class)
                .addPackages(true, "it.eng.parer.sacerlog.entity", "it.eng.parer.sacerlog.viewEntity",
                        "it.eng.paginator", "it.eng.spagoLite", "it.eng.sacerasi.common", "it.eng.sacerasi.exception",
                        "it.eng.sacerasi.entity", "it.eng.sacerasi.grantEntity", "it.eng.sacerasi.viewEntity",
                        "it.eng.sacerasi.slite.gen", "org.apache.commons.lang3")
                .addClass("it.eng.sacerasi.web.helper.ConfigurationHelper")
                .addClass("it.eng.sacerasi.web.helper.dto.PigVGetValParamDto")
                .addClass("it.eng.sacerasi.helper.GenericHelper").addClass("it.eng.sacerasi.helper.HelperInterface")
                .addClass("it.eng.RollbackException").addClass("it.eng.ExpectedException").addClass(clazz);
        return javaArchive;
    }

    public static JavaArchive createSacerLogJar() {
        return ShrinkWrap.create(JavaArchive.class, "sacerlog.jar").addPackages(true, "it.eng.parer.sacerlog")
                .addAsResource(ArquillianUtils.class.getClassLoader().getResource("ejb-jar-sacerlog.xml"),
                        "META-INF/ejb-jar.xml");
    }

    public static JavaArchive createPaginatorJar() {
        return ShrinkWrap.create(JavaArchive.class, "paginator.jar").addPackages(true, "it.eng.paginator")
                .addAsResource(ArquillianUtils.class.getClassLoader().getResource("ejb-jar-paginator.xml"),
                        "META-INF/ejb-jar.xml");
    }

    public static JavaArchive createJbossTimerJar() {
        return ShrinkWrap.create(JavaArchive.class, "timers.jar").addPackages(true, "it.eng.parer.jboss.timer")
                .addPackages(true, "it.eng.parer.jboss.timers")
                .addAsResource(ArquillianUtils.class.getClassLoader().getResource("ejb-jar-timers.xml"),
                        "META-INF/ejb-jar.xml");
    }

    public static EnterpriseArchive createEnterpriseArchive(String archiveName, JavaArchive... modules) {
        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, archiveName + ".ear")
                .addAsResource(EmptyAsset.INSTANCE, "beans.xml");
        for (JavaArchive m : modules) {
            ear.addAsModule(m);
        }
        return ear;
    }

    public static Date[] aDateArray(int n) {
        List list = new ArrayList();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < n; i++) {
            Date date = calendar.getTime();
            list.add(date);
            calendar.add(Calendar.DATE, 1);
        }
        Date[] array = new Date[list.size()];
        list.toArray(array);
        return array;
    }

    public static Timestamp todayTs() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

    public static Timestamp tomorrowTs() {
        return Timestamp.valueOf(LocalDateTime.now().plusDays(1L));
    }

    public static Set emptySet() {
        return new HashSet(0);
    }

    public static int aInt() {
        return new Random().ints(-100, -1).findFirst().getAsInt();
    }

    public static long aLong() {
        return -1L;
    }

    public static BigDecimal aBigDecimal() {
        return BigDecimal.valueOf(-1);
    }

    public static Set<BigDecimal> aSetOfBigDecimal(int size) {
        Set set = new HashSet(size);
        IntStream.range(0, size).forEach(n -> set.add(aBigDecimal()));
        return set;
    }

    public static Set<Long> aSetOfLong(int size) {
        Set set = new HashSet(size);
        IntStream.range(0, size).forEach(n -> set.add(aLong()));
        return set;
    }

    public static Set<String> aSetOfString(int size) {
        Set set = new HashSet(size);
        IntStream.range(0, size).forEach(n -> set.add(aRandomString()));
        return set;
    }

    public static List<BigDecimal> aListOfBigDecimal(int size) {
        return aSetOfBigDecimal(size).stream().collect(Collectors.toList());
    }

    public static List<Long> aListOfLong(int size) {
        List<Long> list = new ArrayList(size);
        IntStream.range(0, size).forEach(n -> list.add(aLong()));
        return list;
    }

    public static List<Integer> aListOfInt(int size) {
        List<Integer> list = new ArrayList(size);
        IntStream.range(0, size).forEach(n -> list.add(aInt()));
        return list;
    }

    public static List<String> aListOfString(int size) {
        List list = new ArrayList(size);
        IntStream.range(0, size).forEach(n -> list.add(aRandomString()));
        return list;
    }

    public static String aRandomString() {
        final int zero = 48;
        final int zed = 122;
        Random random = new Random();

        return random.ints(zero, zed + 1).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(10)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }

    public static String aString() {
        return "TEST_STRING";
    }

    public static void throwExpectedExceptionIfNoResultException(Exception e) {
        throwExceptionIf(ExpectedException.class, e, "No entity found", "NoResultException", "ParerNoResultException",
                "it.eng.parer.exception.errors", "java.lang.NullPointerException",
                "java.lang.IndexOutOfBoundsException", "ParerUserError");
    }

    public static void throwRollbackExceptionIfNoResultException(Exception e) {
        throwExceptionIf(RollbackException.class, e, "No entity found", "NoResultException", "ParerNoResultException",
                "it.eng.parer.exception.errors", "java.lang.NullPointerException",
                "java.lang.IndexOutOfBoundsException", "ParerUserError");
    }

    public static void throwExceptionIf(Class exceptioToBeThrow, Exception e, String... messages) {
        for (String m : messages) {
            final String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            if (message.contains(m)) {
                if (ExpectedException.class.equals(exceptioToBeThrow)) {
                    throw new ExpectedException();
                }
                if (RollbackException.class.equals(exceptioToBeThrow)) {
                    throw new RollbackException();
                }
            }
        }
        throw new RuntimeException("Eccezione non ammessa per superare il test");
    }

    public static String[] aStringArray(int size) {
        String[] array = new String[size];
        List list = new ArrayList(size);
        IntStream.range(0, size).forEach(n -> list.add(aRandomString()));
        list.toArray(array);
        return array;
    }

    public static String aFlag() {
        return "1";
    }

    public static Boolean aBoolean() {
        return Boolean.TRUE;
    }

    /** per scopi di Debug **/
    public static void saveArchiveTo(Archive testArchive, String path) {
        testArchive.as(ZipExporter.class).exportTo(new File(path), true);
    }
}
