//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
// 
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
// 
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package gov.nasa.ltl.tests;

import gov.nasa.ltl.graph.Edge;
import gov.nasa.ltl.graph.EmptyVisitor;
import gov.nasa.ltl.graph.Graph;
import gov.nasa.ltl.graph.Node;
import gov.nasa.ltl.graphio.Reader;
import gov.nasa.ltl.graphio.Writer;

import java.io.IOException;


/**
 * DOCUMENT ME!
 */
public class SM2Dot {
  public static void endDigraph () {
    System.out.println("}");
  }

  public static void main (String[] args) {
    if (args.length != 1) {
      System.err.println("usage:");
      System.err.println("\tSM2Dot <filename>");
      System.err.println();
      System.exit(1);
    }

    try {
      Graph<String> g = Reader.read(args[0]);

      startDigraph(args[0]);

      printInit(g.getInit());

      g.forAllNodes(new EmptyVisitor<String>() {
        public void visitNode (Node<String> n) {
          printNode(n);
          n.forAllEdges(new EmptyVisitor<String>() {
            public void visitEdge (Edge<String> e) {
              printEdge(e);
            }
          });
        }
      });
      endDigraph();
    } catch (IOException e) {
      System.err.println("Can't load file: " + args[0]);
      System.exit(1);
    }
  }
  
  public static void printEdge (Edge<String> e) {
    int          id = e.getSource().getId();
    int          nxt = e.getNext().getId();
    String       guard = Writer.formatSMGuard (e.getGuard());
    String       action = e.getAction();
    String       label = e.getStringAttribute("label");

    StringBuilder sb = new StringBuilder();
    sb.append('\t').append(id).append(" -> ").append(nxt).append(" [label=\"");

    if (label != null) {
      sb.append(label);
      sb.append("\\n");
    }

    if (!guard.equals("-")) {
      sb.append(guard);
      if (!action.equals("-")) {
        sb.append('/').append(action).append("\\n");
      } else {
        sb.append("\\n");
      }
    } else if (!action.equals("-")) {
      sb.append(guard).append('/').append(action).append("\\n");
    } else {
      sb.append("true\\n");
    }

    int     nsets = e.getSource().getGraph().getIntAttribute("nsets");
    boolean first = true;

    for (int i = 0; i < nsets; i++) {
      if (e.getBooleanAttribute("acc" + i)) {
        if (first) {
          sb.append('{');
          first = false;
        } else {
          sb.append(',');
        }

        sb.append(i);
      }
    }

    if (!first) {
      sb.append('}');
    }
    sb.append("\"]");

    System.out.println(sb.toString());
  }

  public static void printInit (Node<String> n) {
    System.out.println("\tinit [color=white, label=\"\"];");
    System.out.println("\tinit -> " + n.getId() + ";");
  }

  public static void printNode (Node<String> n) {
    int id = n.getId();

    if (n.getBooleanAttribute("accepting")) {
      System.out.println("\t" + id + " [shape=doublecircle];");
    } else {
      System.out.println("\t" + id + " [shape=circle];");
    }

    String       label = n.getStringAttribute("label");
    StringBuilder sb = new StringBuilder();
    sb.append('\t').append(id).append(" [label=\"");

    if (label != null) {
      sb.append(label);
      sb.append("\\n");
    }

    sb.append(id).append("\\n");

    int     nsets = n.getGraph().getIntAttribute("nsets");
    boolean first = true;

    for (int i = 0; i < nsets; i++) {
      if (n.getBooleanAttribute("acc" + i)) {
        if (first) {
          sb.append('{');
          first = false;
        } else {
          sb.append(',');
        }

        sb.append(i);
      }
    }

    if (!first) {
      sb.append('}');
    }
    sb.append("\"];");

    System.out.println(sb.toString());
  }

  public static void startDigraph (String name) {
    if (name.lastIndexOf('/') != -1) {
      name = name.substring(name.lastIndexOf('/') + 1);
    }

    name = name.replace('.', '_');
    name = name.replace('-', '_');

    System.out.println("digraph " + name + " {");
  }
}
