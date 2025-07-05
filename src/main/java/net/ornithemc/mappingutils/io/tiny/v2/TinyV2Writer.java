package net.ornithemc.mappingutils.io.tiny.v2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import net.ornithemc.mappingutils.io.Mappings;
import net.ornithemc.mappingutils.io.Mappings.ClassMapping;
import net.ornithemc.mappingutils.io.Mappings.FieldMapping;
import net.ornithemc.mappingutils.io.Mappings.Mapping;
import net.ornithemc.mappingutils.io.Mappings.MethodMapping;
import net.ornithemc.mappingutils.io.Mappings.ParameterMapping;
import net.ornithemc.mappingutils.io.tiny.TinyMappingsWriter;

public class TinyV2Writer extends TinyMappingsWriter {

	public static void write(Path path, Mappings mappings) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
			write(writer, mappings);
		} catch (Exception e) {
			throw new IOException("error writing " + path.toString(), e);
		}
	}

	public static void write(BufferedWriter writer, Mappings mappings) throws IOException {
		new TinyV2Writer(writer, mappings).write();
	}

	private int indents;

	private TinyV2Writer(BufferedWriter writer, Mappings mappings) {
		super(writer, mappings);
	}

	@Override
	protected void writeHeader() throws IOException {
		writer.write(TinyV2Format.FORMAT);
		writer.write(TAB);
		writer.write(TinyV2Format.VERSION);
		writer.write(TAB);
		writer.write(TinyV2Format.MINOR_VERSION);
		writer.write(TAB);
		writer.write(mappings.getSrcNamespace().toString());
		writer.write(TAB);
		writer.write(mappings.getDstNamespace().toString());
		writer.newLine();
	}

	@Override
	protected void writeMappings() throws IOException {
		for (ClassMapping c : mappings.getTopLevelClasses()) {
			writeClass(c);
		}
	}

	private void writeClass(ClassMapping c) throws IOException {
		indent(TinyV2Format.CLASS_INDENTS);

		writer.write(TinyV2Format.CLASS);
		writer.write(TAB);
		writer.write(c.src());
		writer.write(TAB);
		writer.write(c.get().isEmpty() ? c.src() : c.getComplete());
		writer.newLine();

		writeJavadoc(c);

		for (FieldMapping f : c.getFields()) {
			writeField(f);
		}
		for (MethodMapping m : c.getMethods()) {
			writeMethod(m);
		}
		for (ClassMapping cc : c.getClasses()) {
			writeClass(cc);
		}
	}

	private void writeField(FieldMapping f) throws IOException {
		indent(TinyV2Format.FIELD_INDENTS);

		writer.write(TinyV2Format.FIELD);
		writer.write(TAB);
		writer.write(f.getDesc());
		writer.write(TAB);
		writer.write(f.src());
		writer.write(TAB);
		writer.write(f.get().isEmpty() ? f.src() : f.get());
		writer.newLine();

		writeJavadoc(f);
	}

	private void writeMethod(MethodMapping m) throws IOException {
		indent(TinyV2Format.METHOD_INDENTS);

		writer.write(TinyV2Format.METHOD);
		writer.write(TAB);
		writer.write(m.getDesc());
		writer.write(TAB);
		writer.write(m.src());
		writer.write(TAB);
		writer.write(m.get().isEmpty() ? m.src() : m.get());
		writer.newLine();

		writeJavadoc(m);

		for (ParameterMapping p : m.getParameters()) {
			writeParameter(p);
		}

		for (ParameterMapping p : m.getLocals()) {
			writeParameter(p);
		}
	}

	private void writeParameter(ParameterMapping p) throws IOException {
		indent(TinyV2Format.PARAMETER_INDENTS);

		writer.write(p.isLocal() ? TinyV2Format.LOCAL : TinyV2Format.PARAMETER);
		writer.write(TAB);
		writer.write(Integer.toString(p.getIndex()));
		writer.write(TAB);
		writer.write(p.src());
		writer.write(TAB);
		writer.write(p.get());
		writer.newLine();

		writeJavadoc(p);
	}

	private void writeJavadoc(Mapping mapping) throws IOException {
		String jav = mapping.getJavadoc();

		if (!jav.isEmpty()) {
			indents++;

			indent();
			writer.write(TinyV2Format.COMMENT);
			writer.write(TAB);
			writer.write(jav);
			writer.newLine();

			indents--;
		}
	}

	private void indent(int indents) throws IOException {
		this.indents = indents;
		indent();
	}

	private void indent() throws IOException {
		for (int i = 0; i < indents; i++) {
			writer.write(TAB);
		}
	}
}
