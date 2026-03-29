package io.github.olaflaitinen.schneileopard.graph

import io.github.olaflaitinen.schneileopard.core.*
import org.scalatest.funsuite.AnyFunSuite

class InteractionGraphTest extends AnyFunSuite:

  test("InteractionGraph creation from edges") {
    val edges = Set(
      InteractionEdge(GeneId("G1"), GeneId("G2")),
      InteractionEdge(GeneId("G2"), GeneId("G3"))
    )
    val graph = InteractionGraph.fromEdges(edges)
    assert(graph.nodeCount == 3)
    assert(graph.edgeCount == 2)
  }

  test("InteractionGraph neighbors query") {
    val edges = Set(
      InteractionEdge(GeneId("G1"), GeneId("G2")),
      InteractionEdge(GeneId("G1"), GeneId("G3")),
      InteractionEdge(GeneId("G2"), GeneId("G3"))
    )
    val graph = InteractionGraph.fromEdges(edges)

    val g1Neighbors = graph.neighbors(GeneId("G1"))
    assert(g1Neighbors.contains(GeneId("G2")))
    assert(g1Neighbors.contains(GeneId("G3")))
    assert(g1Neighbors.size == 2)
  }

  test("InteractionGraph connected components") {
    val edges = Set(
      InteractionEdge(GeneId("G1"), GeneId("G2")),
      InteractionEdge(GeneId("G2"), GeneId("G3")),
      InteractionEdge(GeneId("G4"), GeneId("G5"))
    )
    val graph = InteractionGraph.fromEdges(edges)

    val components = graph.connectedComponents
    assert(components.length == 2)
    assert(components.exists(_.size == 3))
    assert(components.exists(_.size == 2))
  }

  test("InteractionGraph edge queries") {
    val edges = Set(
      InteractionEdge(GeneId("G1"), GeneId("G2"), Some(0.8)),
      InteractionEdge(GeneId("G2"), GeneId("G3"), Some(0.6))
    )
    val graph = InteractionGraph.fromEdges(edges)

    assert(graph.hasEdge(GeneId("G1"), GeneId("G2")))
    assert(graph.hasEdge(GeneId("G2"), GeneId("G1")))
    assert(!graph.hasEdge(GeneId("G1"), GeneId("G3")))
  }

  test("InteractionGraph single node") {
    val edges = Set(InteractionEdge(GeneId("G1"), GeneId("G1")))
    val graph = InteractionGraph.fromEdges(edges)
    assert(graph.nodeCount == 1)
    assert(graph.edgeCount == 1)
  }

  test("InteractionGraph empty graph") {
    val graph = InteractionGraph(Set.empty, Set.empty)
    assert(graph.nodeCount == 0)
    assert(graph.edgeCount == 0)
    assert(graph.connectedComponents.isEmpty)
  }

  test("InteractionGraph incident edges") {
    val edges = Set(
      InteractionEdge(GeneId("G1"), GeneId("G2"), edgeType = Some("binding")),
      InteractionEdge(GeneId("G1"), GeneId("G3"), edgeType = Some("regulation")),
      InteractionEdge(GeneId("G2"), GeneId("G4"))
    )
    val graph = InteractionGraph.fromEdges(edges)

    val g1Incident = graph.incidentEdges(GeneId("G1"))
    assert(g1Incident.size == 2)

    val g2Incident = graph.incidentEdges(GeneId("G2"))
    assert(g2Incident.size == 2)
  }
