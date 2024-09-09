import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

//bublik rotation
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Создаем тор
        TriangleMesh mesh = createTorusMesh(0.5f, 0.2f, 40, 20);
        MeshView torus = new MeshView(mesh);
        torus.setDrawMode(DrawMode.LINE);
        torus.setCullFace(CullFace.NONE); // Отключаем удаление невидимых граней

        // Создаем сцену
        Group root = new Group(torus);
        Scene scene = new Scene(root, 800, 600, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.BLACK);

        // Добавляем вращение
        Rotate xRotate = new Rotate(0, Rotate.X_AXIS);
        Rotate yRotate = new Rotate(0, Rotate.Y_AXIS);
        Rotate zRotate = new Rotate(0, Rotate.Z_AXIS);
        torus.getTransforms().addAll(xRotate, yRotate, zRotate);

        // Анимация вращения
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                // Вычисляем время, прошедшее с последнего обновления
                double elapsedTime = (now - lastUpdate) / 1_000_000_000.0; // в секундах
                lastUpdate = now;

                // Устанавливаем скорость вращения (меньшее значение = медленнее вращение)
                double rotationSpeed = 20.0; // градусов в секунду

                // Обновляем угол вращения
                xRotate.setAngle(xRotate.getAngle() + elapsedTime * rotationSpeed);
                yRotate.setAngle(yRotate.getAngle() + elapsedTime * rotationSpeed / 2);
                zRotate.setAngle(zRotate.getAngle() + elapsedTime * rotationSpeed / 3);
            }
        };
        timer.start();

        // Настройка камеры
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-5);
        scene.setCamera(camera);

        // Отображаем сцену
        primaryStage.setTitle("Rotating Torus JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TriangleMesh createTorusMesh(float r1, float r2, int n1, int n2) {
        TriangleMesh mesh = new TriangleMesh();

        // Вершины
        for (int i = 0; i <= n1; i++) {
            float theta = (float) (i * 2 * Math.PI / n1);
            for (int j = 0; j <= n2; j++) {
                float phi = (float) (j * 2 * Math.PI / n2);
                float x = (float) ((r1 + r2 * Math.cos(phi)) * Math.cos(theta));
                float y = (float) ((r1 + r2 * Math.cos(phi)) * Math.sin(theta));
                float z = (float) (r2 * Math.sin(phi));
                mesh.getPoints().addAll(x, y, z);
                mesh.getTexCoords().addAll((float) i / n1, (float) j / n2);
            }
        }

        // Грани
        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n2; j++) {
                int p00 = i * (n2 + 1) + j;
                int p01 = p00 + 1;
                int p10 = (i + 1) * (n2 + 1) + j;
                int p11 = p10 + 1;
                mesh.getFaces().addAll(p00, 0, p10, 0, p01, 0);
                mesh.getFaces().addAll(p10, 0, p11, 0, p01, 0);
            }
        }

        return mesh;
    }

    public static void main(String[] args) {
        launch(args);
    }

}