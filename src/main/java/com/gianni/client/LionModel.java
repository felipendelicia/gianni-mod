package com.gianni.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class LionModel extends EntityModel<LionRenderState> {
    private final ModelPart body;
    private final ModelPart saddle;
    private final ModelPart head;
    private final ModelPart mane;
    private final ModelPart collar;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart tail;

    public LionModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.saddle = this.body.getChild("saddle");
        this.head = root.getChild("head");
        this.mane = this.head.getChild("mane");
        this.collar = this.head.getChild("collar");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body",
            CubeListBuilder.create().texOffs(0, 20).addBox(-5.0f, -5.0f, -8.0f, 10.0f, 10.0f, 16.0f),
            PartPose.offset(0.0f, 12.0f, 0.0f));
        body.addOrReplaceChild("saddle",
            CubeListBuilder.create().texOffs(36, 46).addBox(-5.5f, -5.5f, -4.0f, 11.0f, 3.0f, 8.0f),
            PartPose.ZERO);

        PartDefinition head = root.addOrReplaceChild("head",
            CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0f, -4.0f, -6.0f, 8.0f, 8.0f, 6.0f)
                .texOffs(28, 0).addBox(-2.0f, 0.0f, -9.0f, 4.0f, 3.0f, 3.0f)
                .texOffs(28, 6).addBox(-4.0f, -6.0f, -4.0f, 2.0f, 2.0f, 1.0f)
                .texOffs(34, 6).addBox(2.0f, -6.0f, -4.0f, 2.0f, 2.0f, 1.0f),
            PartPose.offset(0.0f, 10.0f, -8.0f));
        head.addOrReplaceChild("mane",
            CubeListBuilder.create().texOffs(40, 10).addBox(-5.5f, -5.5f, -1.0f, 11.0f, 11.0f, 3.0f),
            PartPose.ZERO);
        head.addOrReplaceChild("collar",
            CubeListBuilder.create().texOffs(0, 56).addBox(-3.5f, 2.0f, -4.5f, 7.0f, 1.0f, 7.0f),
            PartPose.ZERO);

        root.addOrReplaceChild("right_front_leg",
            CubeListBuilder.create().texOffs(0, 46).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 10.0f, 3.0f),
            PartPose.offset(-3.5f, 14.0f, -5.5f));
        root.addOrReplaceChild("left_front_leg",
            CubeListBuilder.create().texOffs(12, 46).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 10.0f, 3.0f),
            PartPose.offset(3.5f, 14.0f, -5.5f));
        root.addOrReplaceChild("right_hind_leg",
            CubeListBuilder.create().texOffs(0, 46).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 10.0f, 3.0f),
            PartPose.offset(-3.5f, 14.0f, 5.5f));
        root.addOrReplaceChild("left_hind_leg",
            CubeListBuilder.create().texOffs(12, 46).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 10.0f, 3.0f),
            PartPose.offset(3.5f, 14.0f, 5.5f));

        root.addOrReplaceChild("tail",
            CubeListBuilder.create().texOffs(56, 0).addBox(-0.5f, 0.0f, -0.5f, 1.0f, 9.0f, 1.0f),
            PartPose.offsetAndRotation(0.0f, 9.0f, 8.0f, 0.6f, 0.0f, 0.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    public static LayerDefinition createBabyLayer() {
        return createBodyLayer().apply(MeshTransformer.scaling(0.55f));
    }

    @Override
    public void setupAnim(LionRenderState s) {
        super.setupAnim(s);
        this.head.yRot = s.yRot * Mth.DEG_TO_RAD;
        this.head.xRot = s.xRot * Mth.DEG_TO_RAD;
        this.mane.visible = s.male && !s.isBaby;
        this.saddle.visible = s.saddled;
        this.collar.visible = s.collared && !s.isBaby;

        float pos = s.walkAnimationPos;
        float speed = s.walkAnimationSpeed;
        this.rightFrontLeg.xRot = Mth.cos(pos * 0.6662f) * 1.4f * speed;
        this.leftHindLeg.xRot = this.rightFrontLeg.xRot;
        this.leftFrontLeg.xRot = Mth.cos(pos * 0.6662f + Mth.PI) * 1.4f * speed;
        this.rightHindLeg.xRot = this.leftFrontLeg.xRot;
        this.tail.xRot = 0.6f + Mth.cos(pos * 0.3331f) * 0.25f * speed;
        this.tail.yRot = Mth.sin(s.ageInTicks * 0.08f) * 0.25f + Mth.sin(pos * 0.6662f) * 0.3f * speed;

        if (s.stalking) {
            this.body.y += 3.0f;
            this.head.y += 4.5f;
            this.head.xRot += 0.2f;
            this.rightFrontLeg.y += 3.0f;
            this.leftFrontLeg.y += 3.0f;
            this.rightHindLeg.y += 3.0f;
            this.leftHindLeg.y += 3.0f;
        }

        if (s.sitting || s.resting) {
            this.body.y += 5.0f;
            this.head.y += 5.0f;
            this.rightFrontLeg.xRot = -Mth.HALF_PI;
            this.leftFrontLeg.xRot = -Mth.HALF_PI;
            this.rightHindLeg.xRot = -Mth.HALF_PI;
            this.leftHindLeg.xRot = -Mth.HALF_PI;
            this.rightFrontLeg.y += 5.0f;
            this.leftFrontLeg.y += 5.0f;
            this.rightHindLeg.y += 5.0f;
            this.leftHindLeg.y += 5.0f;
            if (s.resting) {
                this.head.y += 2.0f;
                this.head.xRot += 0.3f;
            }
        }
    }
}
