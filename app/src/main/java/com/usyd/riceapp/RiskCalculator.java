package com.usyd.riceapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RiskCalculator {
    public String calculateRiskByDate(Date plantDate, Date predictDate){
//   no predict date at this stage
        String risk;
//        Date currentDate = new Date(System.currentTimeMillis());
        long diff = predictDate.getTime() - plantDate.getTime();
        int days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        if(days>=0&&days<=30){
            risk = "low";
        }else if(days>30&&days<=60){
            risk = "medium";
        }else{
            risk = "high";
        }
        return risk;
    }
    public String calculateRiskByRiceVariety(ArrayList<String> variety){
        String risk="";
        double risk_h= 0;
        double risk_m = 0;
        double risk_l = 0;
        for(int i = 0;  i< variety.size(); i++){
            if (variety.get(i).equals("Phka Rumdoul")
                    || variety.get(i).equals("Phka Malis")){
                risk_l += 1;
//                Log.e(TAG, "low risk"+String.valueOf(risk_l));
            }else if(variety.get(i).equals("Ka Nhei")
                    || variety.get(i).equals("OM")){
                risk_m += 1.1;
//                Log.e(TAG, "medium risk"+String.valueOf(risk_m));
            }else{
                risk_h += 1.2;
//                Log.e(TAG, "hig risk"+String.valueOf(risk_h));
            }
        }
        if(risk_h>risk_m&&risk_h>risk_l){
            risk = "high";
        }else if(risk_m>risk_h&&risk_m>risk_l){
            risk = "medium";
        }else if(risk_l>risk_h&&risk_l>risk_m){
            risk = "low";
        }
        return risk;
    }
    public String calculateRiskByNitrogen(ArrayList<String> type, int nitrogenAmount){
        String risk;
        double amount = 0;
        for(int i = 0;  i< type.size(); i++){
            if (type.get(i).equals("UREA")){
//                UREA
                amount = amount + 0.46*nitrogenAmount;
            }else if(type.get(i).equals("DAP")){
//                DAP
                amount = amount + 0.18*nitrogenAmount;
            }else{
//                NKP
                amount = amount + 0.23*nitrogenAmount;
            }
        }
        if(amount>=0&&amount<=46){
            risk = "low";
        }else if(amount>46&&amount<=92){
            risk = "medium";
        }else{
            risk = "high";
        }
        return risk;
    }
    public String calculateRiskBySeedingRate(int seedingRate){
        String risk;
        if(seedingRate>=0&&seedingRate<=75){
            risk = "low";
        }else if(seedingRate>75&&seedingRate<=150){
            risk = "medium";
        }else{
            risk = "high";
        }
        return risk;
    }
    public String calculateRiskByTemp(double temperature){
        String risk;
//        0-19
        if(temperature<=19){
            risk = "low";
//            25-28
        }else if(temperature>24&&temperature<=28){
            risk = "high";
//            20 - 24 and 29 >
        }else{ risk = "medium";}
        return risk;
    }
    public String calculateRiskByHumidity(int humidity){
        String risk;
        if(humidity<=69){
            risk = "low";
        }else if(humidity>69&&humidity<=88){
            risk = "medium";
        }else{
            risk = "high";
        }
        return risk;
    }
    public String calculateRiskByRainfall(double rainfall){
        String risk;
//        hourly average from weather data
        rainfall = rainfall*24;
        if(rainfall==0){
            risk = "low";
        }else if(rainfall>0&&rainfall<=200){
            risk = "medium";
        }else{
            risk = "high";
        }
        return risk;
    }
    public String calculateRisk(DfRecord record, DfRiskRecord riskRecord){
        String risk="";
        double risk_h= 0;
        double risk_m = 0;
        double risk_l = 0;
        String risk1 = calculateRiskByDate(record.getPlantDate(), riskRecord.getPredictDate());
        String risk2 = calculateRiskByRiceVariety(record.getRiceVariety());
        String risk3 = calculateRiskByNitrogen(record.getNitrogenType(), record.getNitrogenAmount());
        String risk4 = calculateRiskBySeedingRate(record.getSeedingRate());
        String risk5 = calculateRiskByHumidity(riskRecord.getHumidity());
        String risk6 = calculateRiskByTemp(riskRecord.getTemp());
        String risk7 = calculateRiskByRainfall(riskRecord.getRainfall());
        ArrayList<String> resultList = new ArrayList<>(Arrays.asList(risk1, risk2, risk3, risk4, risk5, risk6, risk7));
        for (int i=0; i<resultList.size(); i++){
            if(resultList.get(i).equals("high")){
                risk_h+=1.2;
            }else if(resultList.get(i).equals("medium")){
                risk_m+=1.1;
            }else{
                risk_l+=1;
            }
        }
        if(risk_h>risk_m&&risk_h>risk_l){
            risk = "high";
        }else if(risk_m>risk_h&&risk_m>risk_l){
            risk = "medium";
        }else if(risk_l>risk_h&&risk_l>risk_m){
            risk = "low";
        }
        return risk;
    }
}
