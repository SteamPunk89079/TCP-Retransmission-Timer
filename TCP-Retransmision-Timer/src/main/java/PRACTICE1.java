
class PRACTICE1 {
    static final float alpha = 0.75F;
    static final int[] sample_rtt = {200, 190, 180, 210, 200, 190, 210, 190, 210, 100, 90, 80, 90, 110, 100, 100, 90, 100, 110, 100};
    static final float[] estimatedRtt = new float[21];

    public PRACTICE1() {
        estimatedRtt[0] = 200;
        for (int i = 0; i < sample_rtt.length; i++) {
            estimatedRtt[i + 1] = estimatedRtt[i] * alpha + (1 - alpha) * sample_rtt[i];
        }
    }

    public float[] getEstimatedRtt() {
        return estimatedRtt;
    }

    public int[] getSample_rtt() {
        return sample_rtt;
    }


}