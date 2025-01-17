/* I declare that this code is my own work */
/* Author <Junxiang Chen> <jchen115@sheffield.ac.uk> */
/*
sub class of SceneGraphObject
 */

import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

public class Table extends SceneGraphObject {
    private Model table_body, table_leg;
    private NameNode tableRoot, tableBodyName, tableLegLeftTop, tableLegLeftBtm, tableLegRightTop, tableLegRightBtm;
    private TransformNode tableTranslate, tableBodyScale, tableLegTransform,
            ltTranslate, lbTranslate, rtTranslate, rbTranslate;
    private ModelNode tableBodyNode, tableLeg_LT_Node, tableLeg_LB_Node, tableLeg_RT_Node, tableLeg_RB_Node;

    private float TABLE_X_POSITION;
    private float TABLE_Y_POSITION;
    private float TABLE_Z_POSITION;

    private float TABLE_BODY_LENGTH;
    private float TABLE_BODY_WIDTH;
    private float TABLE_BODY_HEIGHT;

    private float TABLE_LEG_LENGTH;
    private float TABLE_LEG_WIDTH;
    private float TABLE_LEG_HEIGHT;

    private int[] textureId_Body01;
    private int[] textureId_Leg01;

    public Table(GL3 gl3, Camera camera, Light light1, Light light2, MovingLight movingLight,
                 float TABLE_X_POSITION, float TABLE_Y_POSITION, float TABLE_Z_POSITION,
                 float TABLE_BODY_LENGTH, float TABLE_BODY_WIDTH, float TABLE_BODY_HEIGHT,
                 float TABLE_LEG_LENGTH, float TABLE_LEG_WIDTH, float TABLE_LEG_HEIGHT,
                 int[] textureId_Body01, int[] textureId_Leg01
                 ) {
        super(gl3, camera, light1, light2, movingLight);

        this.TABLE_X_POSITION = TABLE_X_POSITION;
        this.TABLE_Y_POSITION = TABLE_Y_POSITION;
        this.TABLE_Z_POSITION = TABLE_Z_POSITION;

        this.TABLE_BODY_LENGTH = TABLE_BODY_LENGTH;
        this.TABLE_BODY_WIDTH = TABLE_BODY_WIDTH;
        this.TABLE_BODY_HEIGHT = TABLE_BODY_HEIGHT;

        this.TABLE_LEG_LENGTH = TABLE_LEG_LENGTH;
        this.TABLE_LEG_WIDTH = TABLE_LEG_WIDTH;
        this.TABLE_LEG_HEIGHT = TABLE_LEG_HEIGHT;

        this.textureId_Body01 = textureId_Body01;
        this.textureId_Leg01 = textureId_Leg01;
    }

    @Override
    void initialise() {
        tableRoot = new NameNode("table root");
        tableTranslate = new TransformNode(
                "table transform", Mat4Transform.translate(TABLE_X_POSITION, TABLE_Y_POSITION, TABLE_Z_POSITION)
        );

        Mesh tableBodyMesh = new Mesh(gl3, Cube.vertices.clone(), Cube.indices.clone());
        Shader tableBodyShader = new Shader(
                gl3, "shader/vs_table_body.txt", "shader/fs_table_body.txt"
        );
        Material tableBodyMaterial = new Material(
                new Vec3(0.36f, 0.36f, 0.36f),
                new Vec3(1.0f, 1.0f, 1.0f),
                new Vec3(0.2f, 0.15f, 0.15f), 32.0f
        );
        table_body = new Model(
                gl3, camera, light1, light2, movingLight,
                tableBodyShader, tableBodyMaterial, new Mat4(1), tableBodyMesh, textureId_Body01
        );
        Mat4 tableBodyModelMatrix = Mat4Transform.scale(TABLE_BODY_LENGTH, TABLE_BODY_HEIGHT, TABLE_BODY_WIDTH);
        tableBodyScale = new TransformNode("table body scale", tableBodyModelMatrix);
        tableBodyName = new NameNode("body");
        tableBodyNode = new ModelNode("table body", table_body);

        Mesh tableLegMesh = new Mesh(gl3, Cube.vertices.clone(), Cube.indices.clone());
        Shader tableLegShader = new Shader(
                gl3, "shader/vs_table_leg.txt", "shader/fs_table_leg.txt"
        );
        Material tableLegMaterial = new Material(
                new Vec3(0.56f, 0.56f, 0.56f),
                new Vec3(1.0f, 1.0f, 1.0f),
                new Vec3(0.15f, 0.15f, 0.15f), 32.0f
        );
        table_leg = new Model(
                gl3, camera, light1, light2, movingLight,
                tableLegShader, tableLegMaterial, tableBodyModelMatrix, tableLegMesh, textureId_Leg01
        );
        tableLeg_LT_Node = new ModelNode("table leg", table_leg);
        tableLeg_LB_Node = new ModelNode("table leg", table_leg);
        tableLeg_RT_Node = new ModelNode("table leg", table_leg);
        tableLeg_RB_Node = new ModelNode("table leg", table_leg);

        Mat4 tableLegTransformMatrix = Mat4Transform.scale(TABLE_LEG_LENGTH, TABLE_LEG_HEIGHT, TABLE_LEG_WIDTH);
        tableLegTransformMatrix = Mat4.multiply(Mat4Transform.translate(
                0, -4, 0), tableLegTransformMatrix
        );
        tableLegTransform = new TransformNode(
                "table leg transform (scale then translate)", tableLegTransformMatrix
        );
        tableLegLeftTop = new NameNode("table leg left top");
        Mat4 ltTranslateMatrix = Mat4Transform.translate(-TABLE_BODY_LENGTH / 1.6f, 0, -TABLE_BODY_WIDTH / 2);
        ltTranslate = new TransformNode("table leg left top translate", ltTranslateMatrix);
        tableLegLeftBtm = new NameNode("table let left bottom");
        Mat4 lbTranslateMatrix = Mat4Transform.translate(-TABLE_BODY_LENGTH / 1.6f, 0, TABLE_BODY_WIDTH / 2);
        lbTranslate = new TransformNode("table leg left bottom translate", lbTranslateMatrix);
        tableLegRightTop = new NameNode("table leg right top");
        Mat4 rtTranslateMatrix = Mat4Transform.translate(TABLE_BODY_LENGTH / 1.6f, 0, -TABLE_BODY_WIDTH / 2);
        rtTranslate = new TransformNode("table leg right top translate", rtTranslateMatrix);
        tableLegRightBtm = new NameNode("table let right bottom");
        Mat4 rbTranslateMatrix = Mat4Transform.translate(TABLE_BODY_LENGTH / 1.6f, 0, TABLE_BODY_WIDTH / 2);
        rbTranslate = new TransformNode("table leg right bottom translate", rbTranslateMatrix);
    }

    @Override
    void buildTree() {
        tableRoot.addChild(tableTranslate);
            tableTranslate.addChild(tableBodyName);
                tableBodyName.addChild(tableBodyScale);
                    tableBodyScale.addChild(tableBodyNode);
                tableBodyName.addChild(tableLegTransform);
                    tableLegTransform.addChild(tableLegLeftTop);
                        tableLegLeftTop.addChild(ltTranslate);
                            ltTranslate.addChild(tableLeg_LT_Node);
                    tableLegTransform.addChild(tableLegLeftBtm);
                        tableLegLeftBtm.addChild(lbTranslate);
                            lbTranslate.addChild(tableLeg_LB_Node);
                    tableLegTransform.addChild(tableLegRightTop);
                        tableLegRightTop.addChild(rtTranslate);
                            rtTranslate.addChild(tableLeg_RT_Node);
                    tableLegTransform.addChild(tableLegRightBtm);
                        tableLegRightBtm.addChild(rbTranslate);
                            rbTranslate.addChild(tableLeg_RB_Node);
    }

    @Override
    void update() {
        tableRoot.update();
    }

    @Override
    void dispose(GL3 gl3) {
        table_body.dispose(gl3);
        table_leg.dispose(gl3);
    }

    @Override
    public void draw(GL3 gl3) {
        tableRoot.draw(gl3);
    }
}
