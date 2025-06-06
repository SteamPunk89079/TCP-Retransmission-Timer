import javax.swing.*;
import java.awt.*;

public class MyPanel extends JPanel {
    private final int GRID_SIZE = 50;
    private float[] value1;
    private int[] value2;
    private float[] timeout1;
    private float[] timeout2;
    private final int MARGIN = 30;

    public MyPanel() {
        this.setBackground(Color.lightGray);
        PRACTICE1 p1 = new PRACTICE1();
        PRACTICE2 p2 = new PRACTICE2();
        value1 = p1.getEstimatedRtt();
        value2 = p1.getSample_rtt();
        timeout1 = p2.getTimeout1();
        timeout2 = p2.getTimeout2();

        p2.print();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int drawWidth = getWidth() - 2 * MARGIN;
        int drawHeight = getHeight() - 2 * MARGIN;

        drawGrid(g2d, drawWidth, drawHeight);

        float[] scaling = calculateScaling(drawHeight);
        float minValue = scaling[0];
        float maxValue = scaling[1];
        float yScale = scaling[2];

        drawDataset(g2d, value1, minValue,yScale, Color.BLUE, "Estimated", drawWidth);
        drawDataset(g2d, value2, minValue, yScale, Color.RED, "Sample", drawWidth);
        drawDataset(g2d, timeout1, minValue,yScale,Color.GREEN , "Timeout (φ=4)", drawWidth);
        drawDataset(g2d, timeout2,minValue, yScale, Color.MAGENTA, "Timeout (φ=2)", drawWidth);

        drawAxisLabels(g2d, minValue, maxValue, yScale, drawWidth, drawHeight);
    }

    private void drawGrid(Graphics2D g2d, int drawWidth, int drawHeight) {
        g2d.setColor(new Color(220, 220, 220));
        for (int x = 0; x <= drawWidth; x += GRID_SIZE) {
            g2d.drawLine(MARGIN + x, MARGIN,MARGIN + x, MARGIN + drawHeight);
        }
        for (int y = 0; y <= drawHeight; y += GRID_SIZE / 2) {
            g2d.drawLine(MARGIN, MARGIN + y,MARGIN + drawWidth, MARGIN + y);
        }
    }

    private float[] calculateScaling(int drawHeight) {
        float minValue=Float.MAX_VALUE;
        float maxValue=Float.MIN_VALUE;
        if (value1 != null) {
            for (float value : value1) {
                minValue=Math.min(minValue, value);

                maxValue= Math.max(maxValue, value);
            }
        }if (value2 != null) {
            for (int value : value2) {
                minValue = Math.min(minValue, value);
                maxValue = Math.max(maxValue, value);
            }
        }if (timeout1 != null) {
            for (float value : timeout1) {
                minValue = Math.min(minValue, value);
                maxValue = Math.max(maxValue, value);
            }
        }if (timeout2 != null) {
            for (float value : timeout2) {
                minValue = Math.min(minValue, value);
                maxValue = Math.max(maxValue, value);
            }
        }
        float range =maxValue - minValue;
        float padding =range *0.1f;
        minValue-=padding;
        maxValue += padding;

        float yScale = drawHeight / (maxValue - minValue);
        return new float[]{minValue,maxValue, yScale};
    }
    private void drawDataset(Graphics2D g2d, float[] values, float minValue, float yScale, Color color, String label, int drawWidth) {
        if (values == null || values.length < 2) return;

        int pointSize = 6;
        float xScale = (float) drawWidth / (values.length - 1);

        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(1.5f));
        for (int i = 1; i < values.length; i++) {
            int x1 = MARGIN + (int) ((i - 1) * xScale);
            int y1 = MARGIN + (int) ((getHeight() - 2 * MARGIN) - (values[i - 1] - minValue) * yScale);
            int x2 = MARGIN + (int) (i * xScale);
            int y2 = MARGIN + (int) ((getHeight() - 2 * MARGIN) - (values[i] - minValue) * yScale);
            g2d.drawLine(x1, y1, x2, y2);
        }

        for (int i = 0; i < values.length; i++) {
            int x = MARGIN + (int) (i * xScale);
            int y = MARGIN + (int) ((getHeight() - 2 * MARGIN) - (values[i] - minValue) * yScale);
            g2d.fillOval(x - pointSize / 2, y - pointSize / 2, pointSize, pointSize);
        }

        g2d.drawString(label, MARGIN + 10, MARGIN + 15 + getLabelOffset(color));
    }
    private void drawDataset(Graphics2D g2d, int[] values, float minValue, float yScale, Color color, String label, int drawWidth) {
        if (values == null || values.length < 2) return;

        float[] floatValues = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            floatValues[i] = values[i];
        }
        drawDataset(g2d, floatValues, minValue, yScale, color, label, drawWidth);
    }

    private int getLabelOffset(Color color) {
        if (color == Color.BLUE) return 0;
        if (color == Color.RED) return 15;
        if (color == Color.GREEN) return 30;
        return 45;
    }

    private void drawAxisLabels(Graphics2D g2d, float minValue, float maxValue, float yScale, int drawWidth, int drawHeight) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        for (float value = minValue; value <= maxValue; value += (maxValue - minValue) / 5) {
            int y = MARGIN + (int) (drawHeight - (value - minValue) * yScale);
            g2d.drawString(String.format("%.1f", value), 5, y);
        }
        int maxLength = Math.max(
                Math.max(
                        value1 != null ? value1.length : 0,
                        value2 != null ? value2.length : 0
                ),
                Math.max(
                        timeout1 != null ? timeout1.length : 0,
                        timeout2 != null ? timeout2.length : 0
                ));
        for (int i = 0; i < maxLength; i += 2) {
            int x =MARGIN + (int)((float) i / (maxLength - 1) * drawWidth);
            g2d.drawString(Integer.toString(i), x, getHeight() -MARGIN/ 2);
        }
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("RTT and Timeout Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new MyPanel());
        frame.setVisible(true);
    }
    
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
        System.out.println("-----------------------------------------------------------------------------");
        System.out.printf("%-6s %-10s %-14s %-10s %-12s %-10s%n",
                "Zeit", "SampleRTT", "EstimatedRTT", "Timeout1", "Abweichung", "Timeout2");
        System.out.println("-----------------------------------------------------------------------------");
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
}