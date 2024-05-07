package gp.nimfa.clientserver.UDB_Web.model;

public class Message
{
    public static byte[] messagePackaging(int leftFreq, int rightFreq)
    {
        double a1 = 0.01716;
        double b1 = 0.003807;
        double c1 = -1.811e+06;
        double d1 = -0.01376;

        double U1_pol = a1 * Math.exp(b1 * leftFreq) + c1 * Math.exp(d1 * leftFreq);
        U1_pol = Math.round(U1_pol * 10000.0) / 10000.0;
        System.out.println("A = " + U1_pol);

        double a2 = 0.006056;
        double b2 = 0.003645;
        double c2 = -1.302e+08;
        double d2 = -0.01334;

        double U2_pol = a2 * Math.exp(b2 * rightFreq) + c2 * Math.exp(d2 * rightFreq);
        U2_pol = Math.round(U2_pol * 10000.0) / 10000.0;
        System.out.println("B = " + U2_pol);

        double DA = 0;
        double DB = 0;
        double Vref = 5;
        double VoutA = U1_pol;
        double VoutB = U2_pol;
        double n = 12;

        if (VoutA < 0) VoutA = 0;
        if (VoutA > 5) VoutA = 5;
        DA = (VoutA * Math.pow(2, n)) / Vref;
        if (DA < 0) DA = 0;
        if (DA > Math.pow(2, n)) DA = Math.pow(2, n);
        int daca = (int) Math.floor(DA);//short

        if (U1_pol < 0) U1_pol = 0;
        //numericUpDown_DAC_chA.Value = Convert.ToDecimal(U1_pol * 1000);
        int dacaHigh = (daca >> 8);
        int dacaLow = ((daca << 24) >> 24);
        byte daca_High = (byte) (dacaHigh & 0xFF);
        byte daca_Low = (byte) (dacaLow & 0xFF);

        //B
        if (VoutB < 0) VoutB = 0;
        if (VoutB > 5) VoutB = 5;
        DB = (VoutB * Math.pow(2, n)) / Vref;
        if (DB < 0) DB = 0;
        if (DB > Math.pow(2, n)) DB = Math.pow(2, n);
        int dacb = (int)Math.floor(DB);

        if (U2_pol < 0) U2_pol = 0;
        //numericUpDown_DAC_chB.Value = Convert.ToDecimal(U2_pol * 1000);
        System.out.println("VoutA = " + VoutA);
        System.out.println("VoutB = " + VoutB);
        int dacbHigh = (dacb >> 8);
        int dacbLow = ((dacb << 24) >> 24);
        byte dacb_High = (byte) (dacbHigh & 0xFF);
        byte dacb_Low = (byte) (dacbLow & 0xFF);

        //ChC and ChD
        //...
        byte dacc_High = (byte) (dacaHigh & 0xFF);
        byte dacc_Low = (byte) (dacaLow & 0xFF);

        byte dacd_High = (byte) (dacbHigh & 0xFF);
        byte dacd_Low = (byte) (dacbLow & 0xFF);

        /*Write data*/
        byte[] dataWrite = new byte[]
                { 0x0A, 0x0B, 0x0C,
                        daca_High, daca_Low,
                        dacb_High, dacb_Low,
                        dacc_High, dacc_Low,
                        dacd_High, dacd_Low
                };
        return dataWrite;
    }

    //Вводим значение напряжения на каждом канале в мВ
    // И получаем это напряжение на каждом канале
    public static byte[] messageVolt(int dacA, int dacB, int dacC, int dacD)
    {
        //ChA
        int Voltage_DACA = dacA;

        int dacaHigh = (Voltage_DACA >> 8);
        int dacaLow = ((Voltage_DACA << 24) >> 24);

        byte daca_High = (byte) (dacaHigh & 0xFF);
        byte daca_Low = (byte) (dacaLow & 0xFF);

        /*ChB*/
        int Voltage_DACB = dacB;

        int dacbHigh = (Voltage_DACB >> 8);
        int dacbLow = ((Voltage_DACB << 24) >> 24);

        byte dacb_High = (byte) (dacbHigh & 0xFF);
        byte dacb_Low = (byte) (dacbLow & 0xFF);

        //ChC
        int Voltage_DACC = dacC;

        int daccHigh = (Voltage_DACC >> 8);
        int daccLow = ((Voltage_DACC << 24) >> 24);

        byte dacc_High = (byte) (daccHigh & 0xFF);
        byte dacc_Low = (byte) (daccLow & 0xFF);
        //ChD
        int Voltage_DACD = dacD;

        int dacdHigh = (Voltage_DACD >> 8);
        int dacdLow = ((Voltage_DACD << 24) >> 24);

        byte dacd_High = (byte) (dacdHigh & 0xFF);
        byte dacd_Low = (byte) (dacdLow & 0xFF);

        /*Write data*/
        byte[] dataWrite = new byte[]
                { 0x0A, 0x0B, 0x0C,
                        daca_High, daca_Low,
                        dacb_High, dacb_Low,
                        dacc_High, dacc_Low,
                        dacd_High, dacd_Low
                };

        return dataWrite;
    }

}
