package helpers

object Helper {

    implicit class ExtendedOperators[T <: Iterable[Any]](val firstSeq: T) {

        //get object of type T with element that are contained on both given Iterable subtypes
        def <=>(secondSeq: T): T = {
            firstSeq.
              filter(firstElement => secondSeq.exists(_ == firstElement)).
              asInstanceOf[T]
        }
    }

}
