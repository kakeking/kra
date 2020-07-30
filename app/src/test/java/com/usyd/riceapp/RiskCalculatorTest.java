package com.usyd.riceapp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import android.content.Context;
import androidx.test.core.app.ApplicationProvider;

@RunWith(MockitoJUnitRunner.class)
public class RiskCalculatorTest {
    RiskCalculator calculator = new RiskCalculator();

    Date currentDate = new Date(System.currentTimeMillis());
    //    test case 1: Days after planting 0, Rice variety  Romdoul, Seeding rate 0, Nitrogen type DAP, Nitrogen amount 0
//    temperature 0, humidity 0, rainfall 0
//    Risk result should be low
    @Test
    public void test_case_1(){
        ArrayList<String> list_rv_1 = new ArrayList<String>(){{
            add("Romdoul");
        }};
        ArrayList<String> list_nt_1 = new ArrayList<String>(){{
            add("DAP");
        }};
        DfRecord test_1 = new DfRecord();
        DfRiskRecord risk_1 = new DfRiskRecord();
        test_1.setPlantDate(currentDate);
        risk_1.setPredictDate(currentDate);
        test_1.setRiceVariety(list_rv_1);
        test_1.setNitrogenType(list_nt_1);
        test_1.setSeedingRate(0);
        test_1.setNitrogenAmount(0);
        risk_1.setTemp(0);
        risk_1.setHumidity(0);
        risk_1.setRainfall(0);
        String test_1_result = calculator.calculateRisk(test_1, risk_1);
        assertEquals("low", test_1_result);
    }
    //    test case 2: Days after planting 30, Rice variety  Malis, Seeding rate 75, Nitrogen type NPK, Nitrogen amount 200
//    temperature 19.2, humidity 69, rainfall 1.2
//    Risk result should be low
    @Test
    public void test_case_2(){
        Date plant_date = new Date(System.currentTimeMillis()- TimeUnit.DAYS.toMillis(30));
        ArrayList<String> list_rv_2 = new ArrayList<String>(){{
            add("Phka Malis");
        }};
        ArrayList<String> list_nt_2 = new ArrayList<String>(){{
            add("NPK");
        }};
        DfRecord test_2 = new DfRecord();
        DfRiskRecord risk_2 = new DfRiskRecord();
        test_2.setPlantDate(plant_date);
        risk_2.setPredictDate(currentDate);
        test_2.setRiceVariety(list_rv_2);
        test_2.setNitrogenType(list_nt_2);
        test_2.setSeedingRate(75);
        test_2.setNitrogenAmount(200);
        risk_2.setTemp(19.2);
        risk_2.setHumidity(69);
        risk_2.setRainfall(1.2);
        String test_2_result = calculator.calculateRisk(test_2, risk_2);
        assertEquals("low", test_2_result);
    }
    //    test case 3: Days after planting 15, Rice variety  Ka nhei, Seeding rate 40, Nitrogen type DAP, Nitrogen amount 40
//    temperature 20, humidity 70, rainfall 199
//    Risk result should be medium
    @Test
    public void test_case_3(){
        Date plant_date = new Date(System.currentTimeMillis()- TimeUnit.DAYS.toMillis(15));
        ArrayList<String> list_rv_3 = new ArrayList<String>(){{
            add("Ka Nhei");
        }};
        ArrayList<String> list_nt_3 = new ArrayList<String>(){{
            add("DAP");
        }};
        DfRecord test_3 = new DfRecord();
        DfRiskRecord risk_3 = new DfRiskRecord();
        test_3.setPlantDate(plant_date);
        risk_3 .setPredictDate(currentDate);
        test_3.setRiceVariety(list_rv_3);
        test_3.setNitrogenType(list_nt_3);
        test_3.setSeedingRate(40);
        test_3.setNitrogenAmount(40);
        risk_3 .setTemp(20);
        risk_3 .setHumidity(70);
        risk_3 .setRainfall(199);
        String test_3_result = calculator.calculateRisk(test_3, risk_3);
        assertEquals("medium", test_3_result);
    }
    //    test case 4: Days after planting 31, Rice variety  OM&Romdoul, Seeding rate 75, Nitrogen type UREA, Nitrogen amount 100
//    temperature 24, humidity 88, rainfall 200.5
//    Risk result should be medium
    @Test
    public void test_case_4(){
        Date plant_date = new Date(System.currentTimeMillis()- TimeUnit.DAYS.toMillis(31));
        ArrayList<String> list_rv_4 = new ArrayList<String>(){{
            add("OM");
            add("Phka Rumdoul");
        }};
        ArrayList<String> list_nt_4 = new ArrayList<String>(){{
            add("UREA");
        }};
        DfRecord test_4 = new DfRecord();
        DfRiskRecord risk_4 = new DfRiskRecord();
        test_4.setPlantDate(plant_date);
        risk_4.setPredictDate(currentDate);
        test_4.setRiceVariety(list_rv_4);
        test_4.setNitrogenType(list_nt_4);
        test_4.setSeedingRate(75);
        test_4.setNitrogenAmount(100);
        risk_4.setTemp(24);
        risk_4.setHumidity(88);
        risk_4.setRainfall(200.5);
        String test_4_result = calculator.calculateRisk(test_4, risk_4);
        assertEquals("medium", test_4_result);
    }
    //  test case 5: Days after planting 60, Rice variety  Ka Nhei, Seeding rate 46, Nitrogen type DAP, Nitrogen amount 300
//    temperature 25, humidity 89, rainfall 200
//    Risk result should be medium
    @Test
    public void test_case_5(){
        Date plant_date = new Date(System.currentTimeMillis()- TimeUnit.DAYS.toMillis(60));
        ArrayList<String> list_rv_5 = new ArrayList<String>(){{
            add("Ka Nhei");
        }};
        ArrayList<String> list_nt_5 = new ArrayList<String>(){{
            add("DAP");
        }};
        DfRecord test_5 = new DfRecord();
        DfRiskRecord risk_5 = new DfRiskRecord();
        test_5.setPlantDate(plant_date);
        risk_5.setPredictDate(currentDate);
        test_5.setRiceVariety(list_rv_5);
        test_5.setNitrogenType(list_nt_5);
        test_5.setSeedingRate(46);
        test_5.setNitrogenAmount(300);
        risk_5.setTemp(25);
        risk_5.setHumidity(89);
        risk_5.setRainfall(200);
        String test_5_result = calculator.calculateRisk(test_5, risk_5);
        assertEquals("medium", test_5_result);
    }
    //   test case 6: Days after planting 46, Rice variety  OM, Seeding rate 80, Nitrogen type UREA, Nitrogen amount 200
//    temperature 28, humidity 100, rainfall 202
//    Risk result should be medium
    @Test
    public void test_case_6(){
        Date plant_date = new Date(System.currentTimeMillis()- TimeUnit.DAYS.toMillis(46));
        ArrayList<String> list_rv_6 = new ArrayList<String>(){{
            add("OM");
        }};
        ArrayList<String> list_nt_6 = new ArrayList<String>(){{
            add("UREA");
        }};
        DfRecord test_6 = new DfRecord();
        DfRiskRecord risk_6 = new DfRiskRecord();
        test_6.setPlantDate(plant_date);
        risk_6.setPredictDate(currentDate);
        test_6.setRiceVariety(list_rv_6);
        test_6.setNitrogenType(list_nt_6);
        test_6.setSeedingRate(80);
        test_6.setNitrogenAmount(200);
        risk_6.setTemp(28);
        risk_6.setHumidity(100);
        risk_6.setRainfall(202);
        String test_6_result = calculator.calculateRisk(test_6, risk_6);
        assertEquals("medium", test_6_result);
    }
    //   test case 7: Days after planting 61, Rice variety  SKO&Malis, Seeding rate 150, Nitrogen type UREA, Nitrogen amount 120
//    temperature 29, humidity 90, rainfall 0
//    Risk result should be high
    @Test
    public void test_case_7(){
        Date plant_date = new Date(System.currentTimeMillis()- TimeUnit.DAYS.toMillis(61));
//        ArrayList<String> list_rv_7 = new ArrayList<String>(){{
//            add("Phka Malis");
//            add("Sen Kraob");
//        }};
        ArrayList<String> list_rv_7 = new ArrayList<>(Arrays.asList("Phka Malis", "Sen Kraob"));
        ArrayList<String> list_nt_7 = new ArrayList<String>(){{
            add("UREA");
        }};
        DfRecord test_7 = new DfRecord();
        DfRiskRecord risk_7 = new DfRiskRecord();
        test_7.setPlantDate(plant_date);
        risk_7.setPredictDate(currentDate);
        test_7.setRiceVariety(list_rv_7);
        test_7.setNitrogenType(list_nt_7);
        test_7.setSeedingRate(150);
        test_7.setNitrogenAmount(120);
        risk_7.setTemp(29);
        risk_7.setHumidity(90);
        risk_7.setRainfall(0);
        String test_7_result = calculator.calculateRisk(test_7, risk_7);
        assertEquals("high", test_7_result);
    }
    //  test case 8: Days after planting 25, Rice variety  Malis, Seeding rate 151, Nitrogen type UREA, Nitrogen amount 280
//    temperature 40.5, humidity 87, rainfall 0
//    Risk result should be low
    @Test
    public void test_case_8(){
        Date plant_date = new Date(System.currentTimeMillis()- TimeUnit.DAYS.toMillis(25));
        ArrayList<String> list_rv_8 = new ArrayList<String>(){{
            add("Phka Malis");
        }};
        ArrayList<String> list_nt_8 = new ArrayList<String>(){{
            add("UREA");
        }};
        DfRecord test_8 = new DfRecord();
        DfRiskRecord risk_8 = new DfRiskRecord();
        test_8.setPlantDate(plant_date);
        risk_8.setPredictDate(currentDate);
        test_8.setRiceVariety(list_rv_8);
        test_8.setNitrogenType(list_nt_8);
        test_8.setSeedingRate(151);
        test_8.setNitrogenAmount(280);
        risk_8.setTemp(40.5);
        risk_8.setHumidity(87);
        risk_8.setRainfall(0);
        String test_8_result = calculator.calculateRisk(test_8, risk_8);
        assertEquals("low", test_8_result);
    }
    //   test case 9: Days after planting 90, Rice variety  SKO, Seeding rate 76, Nitrogen type UREA, Nitrogen amount 220
//    temperature 12, humidity 50, rainfall 0
//    Risk result should be high
    @Test
    public void test_case_9(){
        Date plant_date = new Date(System.currentTimeMillis()- TimeUnit.DAYS.toMillis(90));
        ArrayList<String> list_rv_9 = new ArrayList<String>(){{
            add("Sen Kraob");
        }};
        ArrayList<String> list_nt_9 = new ArrayList<String>(){{
            add("UREA");
        }};
        DfRecord test_9 = new DfRecord();
        DfRiskRecord risk_9 = new DfRiskRecord();
        test_9.setPlantDate(plant_date);
        risk_9.setPredictDate(currentDate);
        test_9.setRiceVariety(list_rv_9);
        test_9.setNitrogenType(list_nt_9);
        test_9.setSeedingRate(76);
        test_9.setNitrogenAmount(220);
        risk_9.setTemp(12);
        risk_9.setHumidity(50);
        risk_9.setRainfall(0);
        String test_9_result = calculator.calculateRisk(test_9, risk_9);
        assertEquals("high", test_9_result);
    }
    //    test case 10: Days after planting 80, Rice variety  Srange, Seeding rate 200, Nitrogen type UREA, Nitrogen amount 240
//    temperature 32, humidity 79, rainfall 100
//    Risk result should be high
    @Test
    public void test_case_10(){
        Date plant_date = new Date(System.currentTimeMillis()- TimeUnit.DAYS.toMillis(80));
        ArrayList<String> list_rv_10 = new ArrayList<String>(){{
            add("Srangae");
        }};
        ArrayList<String> list_nt_10 = new ArrayList<String>(){{
            add("UREA");
        }};
        DfRecord test_10 = new DfRecord();
        DfRiskRecord risk_10 = new DfRiskRecord();
        test_10.setPlantDate(plant_date);
        risk_10.setPredictDate(currentDate);
        test_10.setRiceVariety(list_rv_10);
        test_10.setNitrogenType(list_nt_10);
        test_10.setSeedingRate(200);
        test_10.setNitrogenAmount(240);
        risk_10.setTemp(32);
        risk_10.setHumidity(79);
        risk_10.setRainfall(100);
        String test_10_result = calculator.calculateRisk(test_10,risk_10);
        assertEquals("high", test_10_result);
    }
    //   test case 11: Days after planting 4, Rice variety  OM&SKO, Seeding rate 77, Nitrogen type NPK, Nitrogen amount 180
//    temperature 26.5, humidity 70, rainfall 2.1
//    Risk result should be medium
    @Test
    public void test_case_11(){
        Date plant_date = new Date(System.currentTimeMillis()- TimeUnit.DAYS.toMillis(4));
        ArrayList<String> list_rv_11 = new ArrayList<String>(){{
            add("OM");
            add("Sen Kraob");
        }};
        ArrayList<String> list_nt_11 = new ArrayList<String>(){{
            add("NPK");
        }};
        DfRecord test_11 = new DfRecord();
        DfRiskRecord risk_11 = new DfRiskRecord();
        test_11.setPlantDate(plant_date);
        risk_11.setPredictDate(currentDate);
        test_11.setRiceVariety(list_rv_11);
        test_11.setNitrogenType(list_nt_11);
        test_11.setSeedingRate(77);
        test_11.setNitrogenAmount(180);
        risk_11.setTemp(26.5);
        risk_11.setHumidity(70);
        risk_11.setRainfall(2.1);
        String test_11_result = calculator.calculateRisk(test_11,risk_11);
        assertEquals("medium", test_11_result);
    }
}
