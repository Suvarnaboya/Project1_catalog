import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

class PolynomialCode {

    // Decode the base-encoded value
    private static BigInteger decodeValue(String encodedValue, int base) {
        return new BigInteger(encodedValue, base);
    }

    // Parse JSON file and extract points
    private static List<DataPoint> extractPointsFromJson(String filePath) throws Exception {
        List<DataPoint> dp = new ArrayList<>();

        try (FileReader reader = new FileReader(filePath)) {
            JSONObject jsonObject = new JSONObject(new JSONTokener(reader));
            JSONObject keyInfo = jsonObject.getJSONObject("keys");
            int numPoints = keyInfo.getInt("n");
            int r = keyInfo.getInt("k");

            for (String key : jsonObject.keySet()) {
                if (!key.equals("keys")) {
                    int xValue = Integer.parseInt(key);
                    JSONObject pd = jsonObject.getJSONObject(key);
                    int base = pd.getInt("base");
                    String encodedY = pd.getString("value");
                    BigInteger yValue = decodeValue(encodedY, base);
                    dp.add(new DataPoint(xValue, yValue));
                }
            }
        }
        return dp;
    }

    // Lagrange interpolation to find the constant term (y-intercept) of the polynomial
    private static BigInteger calculateConstantTerm(List<DataPoint> dp) {
        BigInteger c= BigInteger.ZERO;

        for (int i = 0; i < dp.size(); i++) {
            BigInteger termN = dp.get(i).yValue;
            BigInteger termD = BigInteger.ONE;

            for (int j = 0; j < dp.size(); j++) {
                if (i != j) {
                    termN = termN.multiply(BigInteger.valueOf(-dp.get(j).xValue));
                    termD = termD.multiply(BigInteger.valueOf(dp.get(i).xValue - dp.get(j).xValue));
                }
            }
            c = c.add(termN.divide(termD));
        }
        return c;
    }

    public static void main(String[] args) {
        try {
            String testFile1 = "tc1.json";
            String testFile2 = "tc2.json";
            List<DataPoint> points1 = extractPointsFromJson(testFile1);
            BigInteger constantTerm1 = calculateConstantTerm(points1);
            System.out.println("Test Case1 Output: " + constantTerm1);
            List<DataPoint> points2 = extractPointsFromJson(testFile2);
            BigInteger constantTerm2 = calculateConstantTerm(points2);
            System.out.println("Test Case2 Output: " + constantTerm2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Nested class to represent a data point (x, y)
    static class DataPoint {
        int xValue;
        BigInteger yValue;

        DataPoint(int xValue, BigInteger yValue) {
            this.xValue = xValue;
            this.yValue = yValue;
        }
    }
}
