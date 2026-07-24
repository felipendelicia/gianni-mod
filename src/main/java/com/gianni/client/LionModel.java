package com.gianni.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class LionModel extends EntityModel<LionRenderState> {
    private final ModelPart bodyFront;
    private final ModelPart saddle;
    private final ModelPart bodyBack;
    private final ModelPart belly;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart earR;
    private final ModelPart earL;
    private final ModelPart collar;
    private final ModelPart maneInner;
    private final ModelPart maneMid;
    private final ModelPart maneOuter;
    private final ModelPart rightFrontUpper;
    private final ModelPart leftFrontUpper;
    private final ModelPart rightHindUpper;
    private final ModelPart leftHindUpper;
    private final ModelPart tail1;
    private final ModelPart tail2;

    public LionModel(ModelPart root) {
        super(root);
        this.bodyFront = root.getChild("body_front");
        this.saddle = this.bodyFront.getChild("saddle");
        this.bodyBack = root.getChild("body_back");
        this.belly = root.getChild("belly");
        this.head = root.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.earR = this.head.getChild("ear_r");
        this.earL = this.head.getChild("ear_l");
        this.collar = this.head.getChild("collar");
        this.maneInner = this.head.getChild("mane_inner");
        this.maneMid = this.head.getChild("mane_mid");
        this.maneOuter = this.head.getChild("mane_outer");
        this.rightFrontUpper = root.getChild("right_front_upper");
        this.leftFrontUpper = root.getChild("left_front_upper");
        this.rightHindUpper = root.getChild("right_hind_upper");
        this.leftHindUpper = root.getChild("left_hind_upper");
        this.tail1 = root.getChild("tail1");
        this.tail2 = this.tail1.getChild("tail2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition bodyFront = root.addOrReplaceChild("body_front",
            CubeListBuilder.create().texOffs(40, 34).addBox(-5.5f, -5.0f, -8.0f, 11.0f, 10.0f, 8.0f),
            PartPose.offset(0.0f, 11.0f, 0.0f));
        root.addOrReplaceChild("body_back",
            CubeListBuilder.create().texOffs(0, 34).addBox(-4.5f, -4.5f, 0.0f, 9.0f, 9.0f, 8.0f),
            PartPose.offset(0.0f, 12.0f, 0.0f));
        root.addOrReplaceChild("belly",
            CubeListBuilder.create().texOffs(80, 34).addBox(-3.5f, 0.0f, -6.0f, 7.0f, 2.0f, 12.0f),
            PartPose.offset(0.0f, 15.5f, 0.0f));
        bodyFront.addOrReplaceChild("saddle",
            CubeListBuilder.create().texOffs(64, 0).addBox(-5.5f, -6.0f, -6.5f, 11.0f, 3.0f, 9.0f),
            PartPose.ZERO);

        PartDefinition head = root.addOrReplaceChild("head",
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, -4.0f, -7.0f, 8.0f, 8.0f, 7.0f),
            PartPose.offset(0.0f, 9.5f, -8.0f));
        head.addOrReplaceChild("snout",
            CubeListBuilder.create().texOffs(30, 0).addBox(-2.0f, -1.0f, -4.0f, 4.0f, 3.0f, 4.0f),
            PartPose.offset(0.0f, 0.5f, -7.0f));
        head.addOrReplaceChild("jaw",
            CubeListBuilder.create().texOffs(30, 8).addBox(-2.0f, 0.0f, -3.5f, 4.0f, 2.0f, 3.0f),
            PartPose.offset(0.0f, 2.5f, -7.0f));
        head.addOrReplaceChild("ear_r",
            CubeListBuilder.create().texOffs(46, 0).addBox(-1.0f, -1.5f, -0.5f, 2.0f, 3.0f, 1.0f),
            PartPose.offsetAndRotation(-3.0f, -4.5f, -2.5f, 0.0f, 0.0f, -0.3f));
        head.addOrReplaceChild("ear_l",
            CubeListBuilder.create().texOffs(52, 0).addBox(-1.0f, -1.5f, -0.5f, 2.0f, 3.0f, 1.0f),
            PartPose.offsetAndRotation(3.0f, -4.5f, -2.5f, 0.0f, 0.0f, 0.3f));
        head.addOrReplaceChild("collar",
            CubeListBuilder.create().texOffs(64, 16).addBox(-3.5f, 2.5f, -4.5f, 7.0f, 2.0f, 7.0f),
            PartPose.ZERO);
        head.addOrReplaceChild("mane_inner",
            CubeListBuilder.create().texOffs(0, 72).addBox(-5.5f, -5.5f, -1.0f, 11.0f, 11.0f, 3.0f),
            PartPose.ZERO);
        head.addOrReplaceChild("mane_mid",
            CubeListBuilder.create().texOffs(28, 72)
                .addBox(-6.0f, -6.0f, 0.5f, 12.0f, 12.0f, 3.0f, new CubeDeformation(0.5f)),
            PartPose.ZERO);
        head.addOrReplaceChild("mane_outer",
            CubeListBuilder.create().texOffs(58, 72)
                .addBox(-6.5f, -6.5f, 2.0f, 13.0f, 13.0f, 3.0f, new CubeDeformation(1.0f)),
            PartPose.ZERO);

        addLeg(root, "right_front", -3.5f, -5.5f, 0, 52, 16, 52);
        addLeg(root, "left_front", 3.5f, -5.5f, 0, 52, 16, 52);
        addLeg(root, "right_hind", -3.5f, 5.5f, 32, 52, 48, 52);
        addLeg(root, "left_hind", 3.5f, 5.5f, 32, 52, 48, 52);

        PartDefinition tail1 = root.addOrReplaceChild("tail1",
            CubeListBuilder.create().texOffs(64, 52).addBox(-1.0f, 0.0f, -1.0f, 2.0f, 6.0f, 2.0f),
            PartPose.offsetAndRotation(0.0f, 9.0f, 8.0f, 0.6f, 0.0f, 0.0f));
        PartDefinition tail2 = tail1.addOrReplaceChild("tail2",
            CubeListBuilder.create().texOffs(74, 52).addBox(-0.5f, 0.0f, -0.5f, 1.0f, 5.0f, 1.0f),
            PartPose.offset(0.0f, 6.0f, 0.0f));
        tail2.addOrReplaceChild("tail_tuft",
            CubeListBuilder.create().texOffs(82, 52).addBox(-1.5f, 4.0f, -1.5f, 3.0f, 4.0f, 3.0f),
            PartPose.ZERO);

        return LayerDefinition.create(mesh, 128, 128);
    }

    private static void addLeg(PartDefinition root, String name, float x, float z,
            int upperU, int upperV, int lowerU, int lowerV) {
        PartDefinition upper = root.addOrReplaceChild(name + "_upper",
            CubeListBuilder.create().texOffs(upperU, upperV).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f),
            PartPose.offset(x, 10.0f, z));
        upper.addOrReplaceChild(name + "_lower",
            CubeListBuilder.create().texOffs(lowerU, lowerV).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 8.0f, 3.0f),
            PartPose.offset(0.0f, 6.0f, 0.0f));
    }

    public static LayerDefinition createBabyLayer() {
        return createBodyLayer().apply(MeshTransformer.scaling(0.55f));
    }

    @Override
    public void setupAnim(LionRenderState s) {
        super.setupAnim(s);
        this.head.yRot = s.yRot * Mth.DEG_TO_RAD;
        this.head.xRot = s.xRot * Mth.DEG_TO_RAD;
        boolean mane = s.male && !s.isBaby;
        this.maneInner.visible = mane;
        this.maneMid.visible = mane;
        this.maneOuter.visible = mane;
        this.saddle.visible = s.saddled;
        this.collar.visible = s.collared && !s.isBaby;
        this.jaw.xRot = s.roaring ? 0.55f : 0.0f;

        float pos = s.walkAnimationPos;
        float speed = s.walkAnimationSpeed;
        this.rightFrontUpper.xRot = Mth.cos(pos * 0.6662f) * 1.2f * speed;
        this.leftHindUpper.xRot = this.rightFrontUpper.xRot;
        this.leftFrontUpper.xRot = Mth.cos(pos * 0.6662f + Mth.PI) * 1.2f * speed;
        this.rightHindUpper.xRot = this.leftFrontUpper.xRot;
        this.tail1.xRot = 0.6f + Mth.cos(pos * 0.3331f) * 0.25f * speed;
        this.tail1.yRot = Mth.sin(s.ageInTicks * 0.08f) * 0.25f + Mth.sin(pos * 0.6662f) * 0.3f * speed;
        this.tail2.xRot = 0.2f + Mth.cos(pos * 0.3331f + 0.5f) * 0.15f * speed;
        this.earR.zRot = -0.3f + Mth.sin(s.ageInTicks * 0.05f) * 0.05f;
        this.earL.zRot = 0.3f - Mth.sin(s.ageInTicks * 0.05f + 1.7f) * 0.05f;

        if (s.rolling) {                      // panza arriba, patas al aire
            this.bodyFront.zRot = Mth.PI;
            this.bodyBack.zRot = Mth.PI;
            this.belly.zRot = Mth.PI;
            this.bodyFront.y += 6.0f;
            this.bodyBack.y += 6.0f;
            this.belly.y -= 4.0f;
            this.head.zRot = Mth.PI * 0.9f;
            this.head.y += 8.0f;
            float w = Mth.sin(s.ageInTicks * 0.5f) * 0.5f;
            this.rightFrontUpper.xRot = -2.5f + w;
            this.leftFrontUpper.xRot = -2.5f - w;
            this.rightHindUpper.xRot = -2.5f - w;
            this.leftHindUpper.xRot = -2.5f + w;
            this.rightFrontUpper.y += 6.0f;
            this.leftFrontUpper.y += 6.0f;
            this.rightHindUpper.y += 6.0f;
            this.leftHindUpper.y += 6.0f;
            return;                           // rolling pisa stalking/sitting
        }

        if (s.stalking) {
            this.bodyFront.y += 3.0f;
            this.bodyBack.y += 3.0f;
            this.belly.y += 3.0f;
            this.head.y += 4.5f;
            this.head.xRot += 0.2f;
            this.rightFrontUpper.y += 3.0f;
            this.leftFrontUpper.y += 3.0f;
            this.rightHindUpper.y += 3.0f;
            this.leftHindUpper.y += 3.0f;
        }

        if (s.sitting || s.resting) {
            this.bodyFront.y += 5.0f;
            this.bodyBack.y += 5.0f;
            this.belly.y += 5.0f;
            this.head.y += 5.0f;
            this.rightFrontUpper.xRot = -Mth.HALF_PI;
            this.leftFrontUpper.xRot = -Mth.HALF_PI;
            this.rightHindUpper.xRot = -Mth.HALF_PI;
            this.leftHindUpper.xRot = -Mth.HALF_PI;
            this.rightFrontUpper.y += 5.0f;
            this.leftFrontUpper.y += 5.0f;
            this.rightHindUpper.y += 5.0f;
            this.leftHindUpper.y += 5.0f;
            if (s.resting) {
                this.head.y += 2.0f;
                this.head.xRot += 0.3f;
            }
        }
    }
}
