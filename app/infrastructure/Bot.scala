package net.mtgto.infrastracture

import java.util.UUID

case class Bot(id: UUID, name: String, filename: String, config: Option[String], enabled: Boolean)
