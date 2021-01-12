package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import lombok.JsonOrder;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import org.mangosdk.spi.ProviderFor;

import java.util.ArrayList;
import java.util.List;

import static lombok.javac.handlers.JavacHandlerUtil.isClass;

@ProviderFor(JavacAnnotationHandler.class)
public class HandlerJsonOrder extends JavacAnnotationHandler<JsonOrder> {
    @Override
    public void handle(AnnotationValues<JsonOrder> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        JavacNode typeNode = annotationNode.up();
        boolean notAClass = !isClass(typeNode);
        if (notAClass) {
            annotationNode.addError("@" + JsonOrder.class.getSimpleName() + " is only supported on a class.");
            return;
        }

        List<String> fields = new ArrayList<String>();
        for (JavacNode field : typeNode.down()) {
            if (field.getKind() == AST.Kind.FIELD) {
                fields.add(field.getName());
            }
        }

        if (fields.size() > 0) {
            gennerate(annotationNode, typeNode, fields);
        }
    }

    private void gennerate(JavacNode annotationNode, JavacNode typeNode, List<String> fields) {
        JavacTreeMaker maker = annotationNode.getTreeMaker();

        com.sun.tools.javac.util.List<JCTree.JCExpression> elems = com.sun.tools.javac.util.List.nil();
        for (int i = fields.size() - 1; i >= 0; i--) {
            elems = elems.prepend(maker.Literal(fields.get(i)));
        }
        JCTree.JCExpression arg = maker.NewArray(null, com.sun.tools.javac.util.List.<JCTree.JCExpression>nil(), elems);

        String annotationTypeFqn = "javax.json.bind.annotation.JsonbPropertyOrder";
        JCTree.JCModifiers mods = ((JCTree.JCClassDecl)typeNode.get()).mods;
        JavacHandlerUtil.addAnnotation(mods, annotationNode, annotationNode.get().pos, annotationNode.get(), annotationNode.getContext(), annotationTypeFqn, arg);
    }
}
