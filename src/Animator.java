import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

public class Animator {
    private float MAX_LENGTH, MAX_WIDTH, MAX_DISTANCE;
    private Vec3 previousPosition = new Vec3(0, 0, 0);
    private Vec3 currentPosition = new Vec3(0, 0, 0);
    private Vec3 previousDirection = new Vec3(1, 0, 0);
    private Vec3 currentDirection = new Vec3(1, 0 ,0);
    private Vec3 crossProduct;
    private double lowerJointDeltaRotateDegree = 0;
    public double lowerJointYCurrentRotateDegree;
    private double lowerJointRotateYVelocity = 0;
    private double lowerJointInitialDegree, upperJointInitialDegree, LAMP_BASE_LENGTH;
    private double LOWER_PRESS_MAX_DELTA_DEGREE = 60;
    private double LOWER_STRETCH_MAX_DELTA_DEGREE = 45;
    private double UPPER_PRESS_MAX_DELTA_DEGREE = 60;
    private double UPPER_STRETCH_MAX_DELTA_DEGREE = 75;
    private double lowerPressTargetDegree;
    private double lowerStretchTargetDegree;
    public double lowerJointZCurrentRotateDegree;
    private double lowerJointRotateZVelocity = 2.4;
    private double upperJointRotateZVelocity = 2.4;
    private double upperPressTargetDegree;
    private double upperStretchTargetDegree;
    public double upperJointZCurrentRotateDegree;
    private double tmpMaxVelocity = 0;
    private double rotateYDegreeCount = 0;
    private double lowerRotateZPressDegreeCount = 0;
    private double upperRotateZPressDegreeCount = 0;
    private double lowerRotateZStretchDegreeCount = 0;
    private double upperRotateZStretchDegreeCount = 0;
    private double ratio;
    private double postLowerPressRatio = 0.6;
    private double postLowerPressDeltaDegree;
    private double postLowerStretchRatio = 0.8;
    private double postLowerStretchDeltaDegree;
    private double postUpperPressDeltaDegree;
    private double jumpHorizonVelocity = 0.24;
    public Mat4 previousTranslateMatrix = new Mat4(1);
    public Mat4 currentTranslateMatrix = new Mat4(1);
    public boolean ANIMATION_GENERATION = false;
    public boolean ANIMATION_PREP_Y_ROTATE = false;
    public boolean ANIMATION_PREP_PRESS = false;
    public boolean ANIMATION_PREP_STRETCH = false;
    public boolean ANIMATION_JUMP = false;
    public boolean ANIMATION_POST_PRESS = false;
    public boolean ANIMATION_POST_STRETCH = false;
    public double startJumpTime;
    public double startPressTime;
    public double startStretchTime;

    private double getCurrentSecond() {
        return System.currentTimeMillis() / 1000.0;
    }

    public Animator(float MAX_LENGTH, float MAX_WIDTH, double lowerJointInitialDegree, double upperJointInitialDegree, double LAMP_BASE_LENGTH) {
        this.MAX_LENGTH = MAX_LENGTH;
        this.MAX_WIDTH = MAX_WIDTH;
        this.MAX_DISTANCE = (float) Math.sqrt(Math.pow((double) this.MAX_LENGTH, 2) + Math.pow((double) this.MAX_WIDTH, 2));
        this.lowerJointInitialDegree = lowerJointInitialDegree;
        this.upperJointInitialDegree = upperJointInitialDegree;
        this.lowerJointZCurrentRotateDegree = lowerJointInitialDegree;
        this.upperJointZCurrentRotateDegree = upperJointInitialDegree;
        this.LAMP_BASE_LENGTH = LAMP_BASE_LENGTH;
    }

    public void generateRandomTargetAngle() {
        if (ANIMATION_GENERATION) {
            generateRandomPosition();
//            previousTranslateMatrix = currentTranslateMatrix;
            currentTranslateMatrix = Mat4Transform.translate(currentPosition.x, 0, currentPosition.z);
            generateLowerJointYRotateDegree();
            ANIMATION_GENERATION = false;
            ANIMATION_PREP_Y_ROTATE = true;
        }
    }

    public void generateRandomPosition() {
        double randomX = Math.random() * (MAX_LENGTH - LAMP_BASE_LENGTH) - (MAX_LENGTH - LAMP_BASE_LENGTH) / 2;
        double randomZ = Math.random() * (MAX_WIDTH - LAMP_BASE_LENGTH) - (MAX_WIDTH - LAMP_BASE_LENGTH) / 2;
        currentPosition = new Vec3((float) randomX, 0, (float) randomZ);
        generatePressAngle();
    }

    public void generateLowerJointYRotateDegree() {
        if (ANIMATION_GENERATION) {
            System.out.println("previous pos  = " + previousPosition);
            System.out.println("current pos   = " + currentPosition);
//            System.out.println("previous dir  = " + previousDirection);
            currentDirection = Vec3.normalize(Vec3.subtract(currentPosition, previousPosition));
//            System.out.println("current dir n = " + Vec3.subtract(currentPosition, previousPosition));
            System.out.println("current dir   = " + currentDirection);
            double cosineDegree = Vec3.dotProduct(previousDirection, currentDirection);
//            System.out.println("cosine degree = " + cosineDegree);
            lowerJointDeltaRotateDegree = Math.toDegrees(Math.acos(cosineDegree));
            System.out.println("degree        = " + lowerJointDeltaRotateDegree);
            crossProduct = Vec3.crossProduct(previousDirection, currentDirection);
//            System.out.println("cross product = " + crossProduct);
        }
    }

    public void generatePressAngle() {
        ratio = Vec3.magnitude(Vec3.subtract(previousPosition, currentPosition)) / MAX_DISTANCE;
        System.out.println("ratio"  + ratio);
        lowerPressTargetDegree = LOWER_PRESS_MAX_DELTA_DEGREE * ratio + lowerJointInitialDegree;
        lowerStretchTargetDegree = lowerJointInitialDegree - LOWER_STRETCH_MAX_DELTA_DEGREE * ratio;
        upperPressTargetDegree = upperJointInitialDegree - UPPER_PRESS_MAX_DELTA_DEGREE * ratio;
        upperStretchTargetDegree = UPPER_STRETCH_MAX_DELTA_DEGREE * ratio + upperJointInitialDegree;
    }

    public double easeAnimation(double startTime, double count, double delta, double ratio) {
        double v;
        if (count <= delta / 1.98888888888) {
            v = (getCurrentSecond() - startTime) * ratio;
            tmpMaxVelocity = v;
        } else {
            v = 2.00002 * tmpMaxVelocity - (getCurrentSecond() - startTime) * ratio;
            if (v < 0) {
                v = 0.05;
            }
        }
        return v;
    }

    /**
     * this function will be executed 60 times per second so we do not need loop
     * @param startTime
     * @return void
     */
    public void updateLowerJointYRotateDegree(double startTime) {
        if (ANIMATION_PREP_Y_ROTATE) {
            if (rotateYDegreeCount <= lowerJointDeltaRotateDegree) {
                lowerJointRotateYVelocity = easeAnimation(startTime, rotateYDegreeCount, lowerJointDeltaRotateDegree, 5);
                rotateYDegreeCount = rotateYDegreeCount + lowerJointRotateYVelocity;
//                System.out.println("rotate y velocity                        = " + lowerJointRotateYVelocity);
                if (crossProduct.y >= 0) {
                    lowerJointYCurrentRotateDegree = lowerJointYCurrentRotateDegree + lowerJointRotateYVelocity;
                } else {
                    lowerJointYCurrentRotateDegree = lowerJointYCurrentRotateDegree - lowerJointRotateYVelocity;
                }
//                TODO: turning head
            } else {
                ANIMATION_PREP_Y_ROTATE = false;
                rotateYDegreeCount = 0;
                ANIMATION_PREP_PRESS = true;
                startPressTime = getCurrentSecond();
            }
//            System.out.println("rotate y degree                          = " + lowerJointYCurrentRotateDegree);
        }
    }

    public void lowerJointPress() {
        if (lowerJointZCurrentRotateDegree < lowerPressTargetDegree) {
//            if (ANIMATION_PREP_PRESS) {
//                lowerJointRotateZVelocity = easeAnimation(startPressTime, lowerRotateZPressDegreeCount,LOWER_PRESS_MAX_DELTA_DEGREE * ratio, 2);
//
//            } else if (ANIMATION_POST_PRESS) {
//                lowerJointRotateZVelocity = easeAnimation(startPressTime, lowerRotateZPressDegreeCount,postLowerPressDeltaDegree, 2);
//            }
//            lowerRotateZPressDegreeCount = lowerRotateZPressDegreeCount + lowerJointRotateZVelocity;
            lowerJointZCurrentRotateDegree = lowerJointZCurrentRotateDegree + lowerJointRotateZVelocity;
            System.out.println("rotate z press velocity                  = " + lowerJointRotateZVelocity);
            System.out.println("rotate z press degree                    = " + lowerJointZCurrentRotateDegree);
            System.out.println("count                                    = " + lowerRotateZPressDegreeCount);
        } else {
            if (ANIMATION_PREP_PRESS) {
                ANIMATION_PREP_PRESS = false;
                ANIMATION_PREP_STRETCH = true;
                lowerRotateZPressDegreeCount = 0;
                lowerRotateZStretchDegreeCount = 0;
                startStretchTime = getCurrentSecond();
            }
        }
    }

    public void lowerJointStretch() {
        if (lowerJointZCurrentRotateDegree > lowerStretchTargetDegree) {
//            if (ANIMATION_PREP_STRETCH) {
//                lowerJointRotateZVelocity = easeAnimation(startStretchTime, lowerRotateZStretchDegreeCount,LOWER_STRETCH_MAX_DELTA_DEGREE * ratio, 2);
////                lowerJointRotateZVelocity = 1;
//
//            } else if (ANIMATION_POST_STRETCH) {
//                lowerJointRotateZVelocity = easeAnimation(startStretchTime, lowerRotateZPressDegreeCount, postLowerStretchDeltaDegree, 2);
////                lowerJointRotateZVelocity = 1;
//            }
//            lowerRotateZStretchDegreeCount = lowerRotateZStretchDegreeCount + lowerJointRotateZVelocity;
            lowerJointZCurrentRotateDegree = lowerJointZCurrentRotateDegree - lowerJointRotateZVelocity;
        } else {
            lowerRotateZPressDegreeCount = 0;
            lowerRotateZStretchDegreeCount = 0;
        }
    }

    public void upperJointPress() {
        if (upperJointZCurrentRotateDegree > upperPressTargetDegree) {
//            if (ANIMATION_PREP_PRESS) {
//                upperJointRotateZVelocity = easeAnimation(startPressTime, upperRotateZPressDegreeCount, UPPER_PRESS_MAX_DELTA_DEGREE * ratio, 2);
//            } else if (ANIMATION_POST_PRESS) {
//                upperJointRotateZVelocity = easeAnimation(startPressTime, upperRotateZPressDegreeCount, postUpperPressDeltaDegree, 2);
//            }
//            upperRotateZPressDegreeCount = upperRotateZPressDegreeCount + upperJointRotateZVelocity;
            upperJointZCurrentRotateDegree = upperJointZCurrentRotateDegree - upperJointRotateZVelocity;
        } else {
            if (ANIMATION_POST_PRESS) {
                ANIMATION_POST_PRESS = false;
                ANIMATION_POST_STRETCH = true;
                upperRotateZPressDegreeCount = 0;
                upperRotateZStretchDegreeCount = 0;
                startStretchTime = getCurrentSecond();
            }
        }
    }

    public void upperJointStretch() {
        if (upperJointZCurrentRotateDegree < upperStretchTargetDegree) {
            if (ANIMATION_PREP_STRETCH) {
//                System.out.println("prep stretch");
//                upperJointRotateZVelocity = easeAnimation(startStretchTime, upperRotateZStretchDegreeCount, UPPER_STRETCH_MAX_DELTA_DEGREE * ratio, 2);
                upperJointRotateZVelocity = 2.4;
            } else if (ANIMATION_POST_STRETCH) {
//                System.out.println("post stretch");
//                upperJointRotateZVelocity = easeAnimation(startStretchTime, upperRotateZStretchDegreeCount, postUpperPressDeltaDegree, 2);
//                upperRotateZStretchDegreeCount = upperRotateZStretchDegreeCount + upperJointRotateZVelocity;
                upperJointRotateZVelocity = 4;
            }
//            System.out.println("upper rotate z stretch degree count" + upperRotateZStretchDegreeCount);
            upperJointZCurrentRotateDegree = upperJointZCurrentRotateDegree + upperJointRotateZVelocity;
            if (!ANIMATION_JUMP && ANIMATION_PREP_STRETCH) {
                System.out.println("jump!");
                if (upperJointZCurrentRotateDegree >= upperJointInitialDegree) {
                    ANIMATION_JUMP = true;
//                    ANIMATION_PREP_STRETCH = false;
                    startJumpTime = getCurrentSecond();
                }
            }
        } else {
            if (ANIMATION_PREP_STRETCH) {
                ANIMATION_PREP_STRETCH = false;
                rotateYDegreeCount = 0;
                upperRotateZPressDegreeCount = 0;
                upperRotateZStretchDegreeCount = 0;
            } else if (ANIMATION_POST_STRETCH) {
                ANIMATION_POST_STRETCH = false;
            }
        }
    }

    public void updateJointJumpZRotateDegree() {
        if (ANIMATION_PREP_PRESS) {
            lowerJointPress();
            upperJointPress();
        } else if (ANIMATION_PREP_STRETCH) {
            lowerJointStretch();
            upperJointStretch();
        } else if (ANIMATION_POST_PRESS) {
            lowerJointPress();
            upperJointPress();
        } else if (ANIMATION_POST_STRETCH) {
            lowerStretchTargetDegree = lowerJointInitialDegree;
            lowerJointStretch();
            upperStretchTargetDegree = upperJointInitialDegree;
            upperJointStretch();
        }
    }

    public void updateJump() {
        if (ANIMATION_JUMP) {
            previousPosition.x = previousPosition.x + (float) jumpHorizonVelocity * currentDirection.x;
            previousPosition.z = previousPosition.z + (float) jumpHorizonVelocity * currentDirection.z;
            double distance = jumpHorizonVelocity * (getCurrentSecond() - startJumpTime) * 60;
            previousPosition.y = - 0.15f * (float) distance * (float) (distance - MAX_DISTANCE * ratio);
            if (previousPosition.y <= 0) {
                previousPosition.y = 0;
            }
            System.out.println("previous pos update" + previousPosition);
            previousTranslateMatrix = Mat4Transform.translate(new Vec3(previousPosition));
//            System.out.println("current time = " + startJumpTime);
//            System.out.println("time         = " + (getCurrentSecond() - startJumpTime));
//            System.out.println("previous translate matrix\n" + previousTranslateMatrix.toString());
            if (previousPosition.y == 0 && distance >= MAX_DISTANCE * ratio) {
                System.out.println("stop!");
                ANIMATION_JUMP = false;
                ANIMATION_POST_PRESS = true;
                startPressTime = getCurrentSecond();
                lowerRotateZPressDegreeCount = 0;
                lowerRotateZStretchDegreeCount = 0;
                upperRotateZPressDegreeCount = 0;
                upperRotateZStretchDegreeCount = 0;
                lowerPressTargetDegree = postLowerPressRatio * LOWER_PRESS_MAX_DELTA_DEGREE * ratio + lowerJointInitialDegree;
                postLowerPressDeltaDegree = Math.abs(lowerPressTargetDegree - lowerJointZCurrentRotateDegree);
                upperPressTargetDegree = upperJointInitialDegree - UPPER_PRESS_MAX_DELTA_DEGREE * ratio * postLowerStretchRatio;
                postUpperPressDeltaDegree = Math.abs(upperPressTargetDegree - upperJointZCurrentRotateDegree);
                previousPosition = currentPosition;
                previousDirection = currentDirection;
            }
        }
    }
}