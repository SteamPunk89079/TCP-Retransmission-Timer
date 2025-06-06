class PRACTICE2 {
    static final float alpha = 0.75F;
    static final float mu = 1.0F;
    static final float phi1 = 4.0F;
    static final float phi2 = 2.0F;
    static final int[] sample_rtt = {200, 190, 180, 210, 200, 190, 210, 190, 210, 100, 90, 80, 90, 110, 100, 100, 90, 100, 110, 100};
    static final float[] estimatedRtt = new float[21];
    static final float[] deviation = new float[21];
    static final float[] timeout1 = new float[21];
    static final float[] timeout2 = new float[21];

    public PRACTICE2() {
        estimatedRtt[0] = 200;
        deviation[0] = 0;
        timeout1[0] = mu * estimatedRtt[0] + phi1 * deviation[0];
        timeout2[0] = mu * estimatedRtt[0] + phi2 * deviation[0];

        for (int i = 0; i < sample_rtt.length; i++) {
            estimatedRtt[i + 1] = alpha *estimatedRtt[i] +(1-alpha)* sample_rtt[i];
            float sampleDeviation = Math.abs(sample_rtt[i] - estimatedRtt[i]);
            deviation[i + 1] = alpha * deviation[i]+ (1-alpha) * sampleDeviation;
            timeout1[i + 1 ] = mu * estimatedRtt[i +1] + phi1 * deviation[i + 1];
            timeout2[i+ 1] = mu * estimatedRtt[i +1 ] + phi2 * deviation[i + 1];
        }
    }

    public float[] getEstimatedRtt() {
        return estimatedRtt;
    }

    public float[] getDeviation() {
        return deviation;
    }

    public float[] getTimeout1() {
        return timeout1;
    }

    public float[] getTimeout2() {
        return timeout2;
    }

    public void print() {
        // Header with proper spacing
        System.out.println("-----------------------------------------------------------------------------");
        System.out.printf("%-6s %-10s %-14s %-10s %-12s %-10s%n",
                "Zeit", "SampleRTT", "EstimatedRTT", "Timeout1", "Abweichung", "Timeout2");
        System.out.println("-----------------------------------------------------------------------------");

        // Data rows with consistent formatting
        for (int i = 0; i < sample_rtt.length; i++) {
            System.out.printf("%-6d %-10d %-14.1f %-10.1f %-12.1f %-10.1f%n",
                    i,
                    sample_rtt[i],
                    estimatedRtt[i + 1],
                    timeout1[i + 1],
                    deviation[i + 1],
                    timeout2[i + 1]);
        }
        System.out.println("-----------------------------------------------------------------------------");
    }
}